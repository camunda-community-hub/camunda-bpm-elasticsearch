/*
 * Copyright 2013 - Christian Lipphardt and camunda services GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.camunda.bpm.elasticsearch.index;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.camunda.bpm.elasticsearch.entity.ElasticSearchProcessInstanceHistoryEntity;
import org.camunda.bpm.elasticsearch.util.ElasticSearchHelper;
import org.camunda.bpm.engine.impl.history.event.HistoricActivityInstanceEventEntity;
import org.camunda.bpm.engine.impl.history.event.HistoricProcessInstanceEventEntity;
import org.camunda.bpm.engine.impl.history.event.HistoricTaskInstanceEventEntity;
import org.camunda.bpm.engine.impl.history.event.HistoricVariableUpdateEventEntity;
import org.camunda.bpm.engine.impl.history.event.HistoryEvent;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.script.ScriptService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ElasticSearchDefaultIndexStrategy extends ElasticSearchIndexStrategy {

  protected static final Logger LOGGER = Logger.getLogger(ElasticSearchDefaultIndexStrategy.class.getName());

  protected static final String ES_INDEX_UPDATE_SCRIPT =
      "if (isActivityInstanceEvent) { if (ctx._source.containsKey(\"activities\")) { ctx._source.activities += value } else { ctx._source.activities = value } };" +
      "if (isTaskInstanceEvent) { if (ctx._source.containsKey(\"tasks\")) { ctx._source.tasks += value } else { ctx._source.tasks = value } };" +
      "if (isVariableUpdateEvent) { if (ctx._source.containsKey(\"variables\")) { ctx._source.variables += value } else { ctx._source.variables = value } };";
  protected static final int WAIT_FOR_RESPONSE = 5;

  public void executeRequest(List<HistoryEvent> historyEvents) {
    for (HistoryEvent historyEvent : historyEvents) {
      executeRequest(historyEvent);
    }
  }

  public void executeRequest(HistoryEvent historyEvent) {
    try {
      if (filterEvents(historyEvent)) {
        return;
      }

      UpdateRequestBuilder updateRequestBuilder = prepareUpdateRequest(historyEvent);

      if (LOGGER.isLoggable(Level.FINE)) {
        LOGGER.fine(ElasticSearchHelper.convertRequestToJson(updateRequestBuilder.request()));
      }

      UpdateResponse updateResponse;
      if (WAIT_FOR_RESPONSE > 0) {
        updateResponse = updateRequestBuilder.get(TimeValue.timeValueSeconds(WAIT_FOR_RESPONSE));
      } else {
        updateResponse = updateRequestBuilder.get();
      }

      if (LOGGER.isLoggable(Level.FINE)) {
        LOGGER.fine("[" + updateResponse.getIndex() +
            "][" + updateResponse.getType() +
            "][update] process instance with id '" + updateResponse.getId() + "'");
        LOGGER.log(Level.FINE, "Source: " + updateResponse.getGetResult().sourceAsString());
      }
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
    }
  }

  protected boolean filterEvents(HistoryEvent historyEvent) {
    if (historyEvent instanceof HistoricProcessInstanceEventEntity ||
        historyEvent instanceof HistoricActivityInstanceEventEntity ||
        historyEvent instanceof HistoricTaskInstanceEventEntity ||
        historyEvent instanceof HistoricVariableUpdateEventEntity) {
      return false;
    }
    return true;
  }

  protected UpdateRequestBuilder prepareUpdateRequest(HistoryEvent historyEvent) throws IOException {
    UpdateRequestBuilder updateRequestBuilder = esClient.prepareUpdate()
        .setIndex(dispatcher.getDispatchTargetIndex(historyEvent))
        .setId(historyEvent.getProcessInstanceId());

    String dispatchTargetType = dispatcher.getDispatchTargetType(historyEvent);
    if (dispatchTargetType != null && !dispatchTargetType.isEmpty()) {
      updateRequestBuilder.setType(dispatchTargetType);
    }

    if (historyEvent instanceof HistoricProcessInstanceEventEntity) {
      prepareHistoricProcessInstanceEventUpdate(historyEvent, updateRequestBuilder);
    } else if (historyEvent instanceof HistoricActivityInstanceEventEntity ||
               historyEvent instanceof HistoricTaskInstanceEventEntity ||
               historyEvent instanceof HistoricVariableUpdateEventEntity) {
      updateRequestBuilder = prepareOtherHistoricEventsUpdateRequest(historyEvent, updateRequestBuilder);
    } else {
      // unknown event - insert...
      throw new IllegalArgumentException("Unknown event detected: '" + historyEvent + "'");
//      LOGGER.warning("Unknown event detected: '" + historyEvent + "'");
    }

    if (LOGGER.isLoggable(Level.FINE)) {
      updateRequestBuilder.setFields("_source");
    }

    return updateRequestBuilder;
  }

  protected UpdateRequestBuilder prepareHistoricProcessInstanceEventUpdate(HistoryEvent historyEvent, UpdateRequestBuilder updateRequestBuilder) throws JsonProcessingException {
    ElasticSearchProcessInstanceHistoryEntity elasticSearchProcessInstanceHistoryEntity =
        ElasticSearchProcessInstanceHistoryEntity.createFromHistoryEvent(historyEvent);

    String event = transformer.transformToJson(elasticSearchProcessInstanceHistoryEntity);

    updateRequestBuilder.setDoc(event).setDocAsUpsert(true);

    return updateRequestBuilder;
  }

  protected UpdateRequestBuilder prepareOtherHistoricEventsUpdateRequest(HistoryEvent historyEvent, UpdateRequestBuilder updateRequestBuilder) throws IOException {
    HashMap<String, Object> scriptParams = new HashMap<String, Object>();

    if (historyEvent instanceof HistoricActivityInstanceEventEntity) {
      scriptParams.put("isActivityInstanceEvent", true);
      scriptParams.put("isTaskInstanceEvent", false);
      scriptParams.put("isVariableUpdateEvent", false);
    } else if (historyEvent instanceof HistoricTaskInstanceEventEntity) {
      scriptParams.put("isActivityInstanceEvent", false);
      scriptParams.put("isTaskInstanceEvent", true);
      scriptParams.put("isVariableUpdateEvent", false);
    } else {
      scriptParams.put("isActivityInstanceEvent", false);
      scriptParams.put("isTaskInstanceEvent", false);
      scriptParams.put("isVariableUpdateEvent", true);
    }

    String eventJson = transformer.transformToJson(historyEvent);
    // needed otherwise the resulting json is not an array/list and the update script throws an error
    List<Map<String,Object>> events = transformer.transformJsonToList("[" + eventJson + "]");
    scriptParams.put("value", events);

    updateRequestBuilder.setScript(ES_INDEX_UPDATE_SCRIPT, ScriptService.ScriptType.INLINE)
        .setScriptParams(scriptParams);

    return updateRequestBuilder;
  }

}
