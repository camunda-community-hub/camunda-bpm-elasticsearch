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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.camunda.bpm.elasticsearch.handler.ElasticSearchHistoryEventHandler;
import org.camunda.bpm.elasticsearch.handler.ElasticSearchTransactionAwareHistoryEventHandler;
import org.camunda.bpm.elasticsearch.index.ElasticSearchDefaultIndexStrategy;
import org.camunda.bpm.elasticsearch.index.ElasticSearchIndexStrategy;
import org.camunda.bpm.elasticsearch.session.ElasticSearchSessionFactory;
import org.camunda.bpm.elasticsearch.util.ClassUtil;
import org.camunda.bpm.elasticsearch.util.ElasticSearchHelper;
import org.camunda.bpm.engine.impl.cfg.AbstractProcessEnginePlugin;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.interceptor.SessionFactory;
import org.elasticsearch.client.Client;

public class ElasticSearchHistoryPlugin extends AbstractProcessEnginePlugin {

  protected static final Logger LOGGER = Logger.getLogger(ElasticSearchHistoryPlugin.class.getName());

  public static final String ES_DEFAULT_HISTORY_EVENT_HANDLER = ElasticSearchTransactionAwareHistoryEventHandler.class.getName();
  public static final String ES_DEFAULT_HISTORY_INDEXING_STRATEGY = ElasticSearchDefaultIndexStrategy.class.getName();

  protected ElasticSearchHistoryEventHandler historyEventHandler;
  protected ElasticSearchHistoryPluginConfiguration historyPluginConfiguration;
  protected ElasticSearchClient elasticSearchClient;
  protected ElasticSearchIndexStrategy indexingStrategy;

  protected String esCluster = null;
  protected String esHost = null;
  protected String esPort = null;
  protected String esIndex = null;
  protected String esIndexingStrategy = ES_DEFAULT_HISTORY_INDEXING_STRATEGY;
  protected String esEventHandler = ES_DEFAULT_HISTORY_EVENT_HANDLER;
  protected Map<String, Object> esProperties = new HashMap<String, Object>();
  protected boolean clientNode = true;
  protected boolean localNode = false;
  protected boolean dataNode = false;

  @Override
  public void preInit(ProcessEngineConfigurationImpl processEngineConfiguration) {
    historyPluginConfiguration = ElasticSearchHistoryPluginConfiguration.readConfigurationFromClasspath();

    setHistoryPluginConfigurationProperties(historyPluginConfiguration);

    // retrieve indexing strategy
    Class<? extends ElasticSearchIndexStrategy> indexingStrategyClass =
        ClassUtil.loadClass(historyPluginConfiguration.getIndexingStrategy(), null, ElasticSearchIndexStrategy.class);
    indexingStrategy = ClassUtil.createInstance(indexingStrategyClass);

    // create es client
    elasticSearchClient = new ElasticSearchClient(historyPluginConfiguration);
    indexingStrategy.setEsClient(elasticSearchClient.get());

    if (historyPluginConfiguration.getEventHandler() == null) {
      historyPluginConfiguration.setEventHandler(ES_DEFAULT_HISTORY_EVENT_HANDLER);
    }

    Class<? extends ElasticSearchHistoryEventHandler> historyEventHandlerClass =
        ClassUtil.loadClass(historyPluginConfiguration.getEventHandler(), null, ElasticSearchHistoryEventHandler.class);
    historyEventHandler = ClassUtil.createInstance(historyEventHandlerClass);

    if(processEngineConfiguration.getCustomSessionFactories() == null) {
      processEngineConfiguration.setCustomSessionFactories(new ArrayList<SessionFactory>());
    }
    processEngineConfiguration.getCustomSessionFactories().add(new ElasticSearchSessionFactory(indexingStrategy));

    validateHistoryPluginConfig();

    processEngineConfiguration.setHistoryEventHandler(historyEventHandler);
  }

  protected void validateHistoryPluginConfig() {
    historyPluginConfiguration.validate();
    ElasticSearchHelper.checkIndex(elasticSearchClient.get(), historyPluginConfiguration.getIndex());
    ElasticSearchHelper.checkTypeAndMapping(elasticSearchClient.get(), historyPluginConfiguration.getIndex(), historyPluginConfiguration.getType());
  }

  protected void setHistoryPluginConfigurationProperties(ElasticSearchHistoryPluginConfiguration historyPluginConfiguration) {
    if (esCluster != null && !esCluster.isEmpty()) {
      historyPluginConfiguration.setEsClusterName(esCluster);
    }
    if (esHost != null && !esHost.isEmpty()) {
      historyPluginConfiguration.setEsHost(esHost);
    }
    if (esPort != null && !esPort.isEmpty()) {
      historyPluginConfiguration.setEsPort(esPort);
    }
    if (esIndex != null && !esIndex.isEmpty()) {
      historyPluginConfiguration.setIndex(esIndex);
    }
    if (esIndexingStrategy != null && !esIndexingStrategy.isEmpty()) {
      historyPluginConfiguration.setIndexingStrategy(esIndexingStrategy);
    }
    if (esEventHandler != null && !esEventHandler.isEmpty()) {
      historyPluginConfiguration.setEventHandler(esEventHandler);
    }
    if (esProperties != null) {
      historyPluginConfiguration.getProperties().putAll(esProperties);
    }

    historyPluginConfiguration.getProperties().put("es.node.client", clientNode);
    historyPluginConfiguration.getProperties().put("es.node.local", localNode);
    historyPluginConfiguration.getProperties().put("es.node.data", dataNode);
  }

  public String getEsEventHandler() {
    return esEventHandler;
  }

  public void setEsEventHandler(String esEventHandler) {
    this.esEventHandler = esEventHandler;
  }

  public String getEsIndexingStrategy() {
    return esIndexingStrategy;
  }

  public void setEsIndexingStrategy(String esIndexingStrategy) {
    this.esIndexingStrategy = esIndexingStrategy;
  }

  public String getEsIndex() {
    return esIndex;
  }

  public void setEsIndex(String esIndex) {
    this.esIndex = esIndex;
  }

  public String getEsPort() {
    return esPort;
  }

  public void setEsPort(String esPort) {
    this.esPort = esPort;
  }

  public String getEsHost() {
    return esHost;
  }

  public void setEsHost(String esHost) {
    this.esHost = esHost;
  }

  public String getEsCluster() {
    return esCluster;
  }

  public void setEsCluster(String esCluster) {
    this.esCluster = esCluster;
  }

  public Map<String, Object> getEsProperties() {
    return esProperties;
  }

  public void setEsProperties(Map<String, Object> esProperties) {
    this.esProperties = esProperties;
  }

  public boolean isClientNode() {
    return clientNode;
  }

  public void setClientNode(boolean clientNode) {
    this.clientNode = clientNode;
  }

  public boolean isLocalNode() {
    return localNode;
  }

  public void setLocalNode(boolean localNode) {
    this.localNode = localNode;
  }

  public boolean isDataNode() {
    return dataNode;
  }

  public void setDataNode(boolean dataNode) {
    this.dataNode = dataNode;
  }

  public ElasticSearchClient getElasticSearchClient() {
    return elasticSearchClient;
  }

  public void setElasticSearchClient(Client client) {
    elasticSearchClient.set(client);
    indexingStrategy.setEsClient(elasticSearchClient.get());
  }
}
