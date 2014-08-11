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

package org.camunda.bpm.elasticsearch.handler;

import org.camunda.bpm.elasticsearch.ElasticSearchHistoryPluginConfiguration;
import org.camunda.bpm.elasticsearch.index.ElasticSearchIndexStrategy;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.history.event.HistoryEvent;
import org.camunda.bpm.engine.impl.history.handler.HistoryEventHandler;

import java.util.List;
import java.util.logging.Logger;

public abstract class ElasticSearchHistoryEventHandler implements HistoryEventHandler {

  protected Logger LOGGER = Logger.getLogger(this.getClass().getName());

  protected ElasticSearchHistoryPluginConfiguration historyPluginConfiguration;
  protected ElasticSearchIndexStrategy indexingStrategy;
  protected ProcessEngineConfigurationImpl processEngineConfiguration;

  @Override
  public abstract void handleEvent(HistoryEvent historyEvent);

  public void handleEvents(List<HistoryEvent> historyEvents) {
    for (HistoryEvent historyEvent : historyEvents) {
      handleEvent(historyEvent);
    }
  }

  public ElasticSearchIndexStrategy getIndexingStrategy() {
    return indexingStrategy;
  }

  public void setIndexingStrategy(ElasticSearchIndexStrategy indexingStrategy) {
    this.indexingStrategy = indexingStrategy;
  }

  public void setProcessEngineConfiguration(ProcessEngineConfigurationImpl processEngineConfiguration) {
    this.processEngineConfiguration = processEngineConfiguration;
  }

  public ProcessEngineConfigurationImpl getProcessEngineConfiguration() {
    return processEngineConfiguration;
  }
}
