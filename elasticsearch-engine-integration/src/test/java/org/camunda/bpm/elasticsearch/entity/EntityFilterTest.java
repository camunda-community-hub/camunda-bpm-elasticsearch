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

package org.camunda.bpm.elasticsearch.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.camunda.bpm.elasticsearch.jackson.JsonTransformer;
import org.camunda.bpm.engine.impl.history.event.HistoryEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class EntityFilterTest {

  private HistoryEvent historyEvent;
  private JsonTransformer jsonTransformer;

  @Before
  public void setUp() {
    jsonTransformer = new JsonTransformer();

    historyEvent = new HistoryEvent();
    historyEvent.setId("id");
    historyEvent.setExecutionId("ExecutionId");
    historyEvent.setProcessDefinitionId("processDefinitionId");
    historyEvent.setProcessInstanceId("processInstanceId");
    historyEvent.setEventType(HistoryEvent.ACTIVITY_EVENT_TYPE_START);

    assertEquals(HistoryEvent.class, historyEvent.getPersistentState());
  }

  @After
  public void tearDown() {
    jsonTransformer = null;
  }

  @Test
  public void shouldTransformHistoryEventToJsonAndApplyMixInFilter() throws JsonProcessingException {
    String json = jsonTransformer.transformToJson(historyEvent);

    assertFalse(json.contains(HistoryEvent.class.getName()));
  }

  @Test
  public void shouldTransformJsonToMapAndApplyMixInFilter() throws IOException {
    String json = jsonTransformer.transformToJson(historyEvent);
    assertFalse(json.contains(HistoryEvent.class.getName()));

    Map<String,Object> jsonMap = jsonTransformer.transformJsonToMap(json);
    assertFalse(jsonMap.containsKey(""));
  }

}
