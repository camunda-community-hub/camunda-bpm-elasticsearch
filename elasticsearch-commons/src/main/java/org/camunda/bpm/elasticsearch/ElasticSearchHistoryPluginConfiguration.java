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

import org.camunda.bpm.elasticsearch.util.JsonHelper;

import java.util.HashMap;

public class ElasticSearchHistoryPluginConfiguration {

  public static final String ES_CAM_BPM_CONFIGURATION = "camunda-bpm-elasticsearch.json";

  public static final String ES_DEFAULT_INDEX_NAME_CAMUNDA_BPM = "camundabpm";
  public static final String ES_DEFAULT_TYPE_NAME_CAMUNDA_BPM = "processinstance";

  public static final String ES_DEFAULT_HOST = "localhost";
  public static final String ES_DEFAULT_PORT_NODE_CLIENT = "9300";
  public static final String ES_DEFAULT_PORT_TRANSPORT_CLIENT = "9300";
  public static final String ES_DEFAULT_CLUSTER_NAME = "elasticsearch";

  private String index;
  private String type;
  private String indexingStrategy;

  private String esHost;
  private String esPort;
  private String esClusterName;
  private boolean transportClient = false;
  private HashMap<String, Object> properties = new HashMap<String, Object>();
  private String eventHandler;

  public String getIndex() {
    if (index == null) {
      index = ES_DEFAULT_INDEX_NAME_CAMUNDA_BPM;
    }
    return index;
  }

  public void setIndex(String index) {
    this.index = index;
  }

  public String getType() {
    if (type == null) {
      type = ES_DEFAULT_TYPE_NAME_CAMUNDA_BPM;
    }
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getIndexingStrategy() {
    return indexingStrategy;
  }

  public void setIndexingStrategy(String indexingStrategy) {
    this.indexingStrategy = indexingStrategy;
  }

  public String getEsHost() {
    if (esHost == null) {
      esHost = ES_DEFAULT_HOST;
    }
    return esHost;
  }

  public void setEsHost(String esHost) {
    this.esHost = esHost;
  }

  public String getEsPort() {
    if (esPort == null) {
      if (isTransportClient()) {
        esPort = ES_DEFAULT_PORT_TRANSPORT_CLIENT;
      } else {
        esPort = ES_DEFAULT_PORT_NODE_CLIENT;
      }
    }
    return esPort;
  }

  public void setEsPort(String esPort) {
    this.esPort = esPort;
  }

  public String getEsClusterName() {
    if (esClusterName == null) {
      esClusterName = ES_DEFAULT_CLUSTER_NAME;
    }
    return esClusterName;
  }

  public void setEsClusterName(String esClusterName) {
    this.esClusterName = esClusterName;
  }

  public boolean isTransportClient() {
    return transportClient;
  }

  public void setTransportClient(boolean transportClient) {
    this.transportClient = transportClient;
  }

  public HashMap<String, Object> getProperties() {
    return properties;
  }

  public void setProperties(HashMap<String, Object> properties) {
    this.properties = properties;
  }

  public String getEventHandler() {
    return eventHandler;
  }

  public void setEventHandler(String eventHandler) {
    this.eventHandler = eventHandler;
  }

  public void validate() {
    StringBuilder sb = new StringBuilder();

    if (getEsClusterName() == null || getEsClusterName().isEmpty()) {

    }
    if (getEsHost() == null || getEsHost().isEmpty()) {

    }
    if (getEsPort() == null || getEsPort().isEmpty()) {

    }
    if (getIndex() == null || getIndex().isEmpty()) {

    }
    if (getType() == null || getType().isEmpty()) {

    }
    if (getIndexingStrategy() == null || getIndexingStrategy().isEmpty()) {

    }
    if (getEventHandler() == null || getEventHandler().isEmpty()) {

    }
  }

  public static ElasticSearchHistoryPluginConfiguration readConfigurationFromClasspath() {
    return readConfigurationFromClasspath(ES_CAM_BPM_CONFIGURATION);
  }

  public static ElasticSearchHistoryPluginConfiguration readConfigurationFromClasspath(String fileName) {
    return JsonHelper.readJsonFromClasspath(ElasticSearchHistoryPluginConfiguration.class, fileName);
  }

}
