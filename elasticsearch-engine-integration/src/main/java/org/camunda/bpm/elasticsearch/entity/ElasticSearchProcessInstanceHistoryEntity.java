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

import org.camunda.bpm.engine.impl.history.event.HistoricActivityInstanceEventEntity;
import org.camunda.bpm.engine.impl.history.event.HistoricProcessInstanceEventEntity;
import org.camunda.bpm.engine.impl.history.event.HistoricTaskInstanceEventEntity;
import org.camunda.bpm.engine.impl.history.event.HistoricVariableUpdateEventEntity;
import org.camunda.bpm.engine.impl.history.event.HistoryEvent;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ElasticSearchProcessInstanceHistoryEntity extends HistoricProcessInstanceEventEntity {

  private List<HistoricActivityInstanceEventEntity> activities = null;
  private List<HistoricTaskInstanceEventEntity> tasks = null;
  private List<HistoricVariableUpdateEventEntity> variables = null;

  public ElasticSearchProcessInstanceHistoryEntity() {
  }

  public static ElasticSearchProcessInstanceHistoryEntity createFromHistoryEvent(HistoryEvent historyEvent) {
    ElasticSearchProcessInstanceHistoryEntity esHistoryEntity = new ElasticSearchProcessInstanceHistoryEntity();

    esHistoryEntity.setId(historyEvent.getId()); // maybe use process instance id here?
    esHistoryEntity.setProcessInstanceId(historyEvent.getProcessInstanceId());

    if (historyEvent instanceof HistoricProcessInstanceEventEntity) {
      HistoricProcessInstanceEventEntity pie = (HistoricProcessInstanceEventEntity) historyEvent;

      esHistoryEntity.setExecutionId(pie.getExecutionId());
      esHistoryEntity.setProcessDefinitionId(pie.getProcessDefinitionId());

      esHistoryEntity.setStartActivityId(pie.getStartActivityId());
      esHistoryEntity.setEndActivityId(pie.getEndActivityId());
      esHistoryEntity.setStartTime(pie.getStartTime());
      esHistoryEntity.setEndTime(pie.getEndTime());
      esHistoryEntity.setDurationInMillis(pie.getDurationInMillis());

      esHistoryEntity.setBusinessKey(pie.getBusinessKey());
      esHistoryEntity.setStartUserId(pie.getStartUserId());
      esHistoryEntity.setDeleteReason(pie.getDeleteReason());
      esHistoryEntity.setSuperProcessInstanceId(pie.getSuperProcessInstanceId());

    } else if (historyEvent instanceof HistoricActivityInstanceEventEntity) {

      HistoricActivityInstanceEventEntity aie = (HistoricActivityInstanceEventEntity) historyEvent;
      esHistoryEntity.addHistoricActivityInstanceEvent(aie);

    } else if (historyEvent instanceof HistoricTaskInstanceEventEntity) {

      HistoricTaskInstanceEventEntity tie = (HistoricTaskInstanceEventEntity) historyEvent;
      esHistoryEntity.addHistoricTaskInstanceEvent(tie);

    } else if (historyEvent instanceof HistoricVariableUpdateEventEntity) {

      HistoricVariableUpdateEventEntity vue = (HistoricVariableUpdateEventEntity) historyEvent;
      esHistoryEntity.addHistoricVariableUpdateEvent(vue);

    } else {
      // unknown event - throw exception or return null?
    }

    return esHistoryEntity;
  }

  public List<HistoricVariableUpdateEventEntity> getVariables() {
    return variables;
  }

  public void setVariables(List<HistoricVariableUpdateEventEntity> variables) {
    this.variables = variables;
  }

  public void addHistoricVariableUpdateEvent(HistoricVariableUpdateEventEntity variableUpdateEvent) {
    if (variables == null) {
      variables = new ArrayList<HistoricVariableUpdateEventEntity>();
    }
    variables.add(variableUpdateEvent);
  }

  public List<HistoricActivityInstanceEventEntity> getActivities() {
    return activities;
  }

  public void setActivities(List<HistoricActivityInstanceEventEntity> activities) {
    this.activities = activities;
  }

  public void addHistoricActivityInstanceEvent(HistoricActivityInstanceEventEntity activityInstanceEvent) {
    if (activities == null) {
      activities = new ArrayList<HistoricActivityInstanceEventEntity>();
    }
    activities.add(activityInstanceEvent);
  }

  public List<HistoricTaskInstanceEventEntity> getTasks() {
    return tasks;
  }

  public void setTasks(List<HistoricTaskInstanceEventEntity> tasks) {
    this.tasks = tasks;
  }

  public void addHistoricTaskInstanceEvent(HistoricTaskInstanceEventEntity taskInstanceEvent) {
    if (tasks == null) {
      tasks = new ArrayList<HistoricTaskInstanceEventEntity>();
    }
    tasks.add(taskInstanceEvent);
  }
}
