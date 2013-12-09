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

package org.camunda.bpm.elasticsearch;

import org.camunda.bpm.elasticsearch.handler.AbstractElasticSearchHistoryEventHandler;
import org.camunda.bpm.elasticsearch.handler.ElasticSearchTransactionAwareHistoryEventHandler;
import org.camunda.bpm.elasticsearch.index.ElasticSearchDefaultIndexStrategy;
import org.camunda.bpm.elasticsearch.index.ElasticSearchIndexStrategy;
import org.camunda.bpm.elasticsearch.util.ClassUtil;
import org.camunda.bpm.elasticsearch.util.ElasticSearchHelper;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.cfg.ProcessEnginePlugin;

import java.util.logging.Logger;

public class ElasticSearchHistoryPlugin implements ProcessEnginePlugin {

  protected static final Logger LOGGER = Logger.getLogger(ElasticSearchHistoryPlugin.class.getName());

  public static final String ES_DEFAULT_HISTORY_EVENT_HANDLER = ElasticSearchTransactionAwareHistoryEventHandler.class.getName();
  public static final String ES_DEFAULT_HISTORY_INDEXING_STRATEGY = ElasticSearchDefaultIndexStrategy.class.getName();

  protected AbstractElasticSearchHistoryEventHandler historyEventHandler;
  protected ElasticSearchHistoryPluginConfiguration historyPluginConfiguration;
  protected ElasticSearchClient elasticSearchClient;

  @Override
  public void preInit(ProcessEngineConfigurationImpl processEngineConfiguration) {
    historyPluginConfiguration = ElasticSearchHistoryPluginConfiguration.readConfigurationFromClasspath();

    // retrieve indexing strategy
    Class<? extends ElasticSearchIndexStrategy> indexingStrategyClass =
        ClassUtil.loadClass(historyPluginConfiguration.getIndexingStrategy(), null, ElasticSearchIndexStrategy.class);
    ElasticSearchIndexStrategy indexingStrategy = ClassUtil.createInstance(indexingStrategyClass);

    elasticSearchClient = new ElasticSearchClient(historyPluginConfiguration);
    indexingStrategy.setEsClient(elasticSearchClient.get());

    if (historyPluginConfiguration.getEventHandler() == null) {
      historyPluginConfiguration.setEventHandler(ES_DEFAULT_HISTORY_EVENT_HANDLER);
    }

    Class<? extends AbstractElasticSearchHistoryEventHandler> historyEventHandlerClass =
        ClassUtil.loadClass(historyPluginConfiguration.getEventHandler(), null, AbstractElasticSearchHistoryEventHandler.class);
    historyEventHandler = ClassUtil.createInstance(historyEventHandlerClass);
    historyEventHandler.setIndexingStrategy(indexingStrategy);
    historyEventHandler.setProcessEngineConfiguration(processEngineConfiguration);

    ElasticSearchHelper.checkIndex(elasticSearchClient.get(), historyPluginConfiguration.getIndex());
    ElasticSearchHelper.checkTypeAndMapping(elasticSearchClient.get(), historyPluginConfiguration.getIndex(), historyPluginConfiguration.getType());

    processEngineConfiguration.setHistoryEventHandler(historyEventHandler);
  }

  @Override
  public void postInit(ProcessEngineConfigurationImpl processEngineConfiguration) {
  }

  @Override
  public void postProcessEngineBuild(ProcessEngine processEngine) {
  }

}
