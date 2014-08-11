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

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ElasticSearchClient {

  protected static final Logger LOGGER = Logger.getLogger(ElasticSearchClient.class.getName());

  protected Client esClient;
  protected Node esNode;

  protected ElasticSearchHistoryPluginConfiguration historyPluginConfiguration;

  public ElasticSearchClient(ElasticSearchHistoryPluginConfiguration historyPluginConfiguration) {
    this.historyPluginConfiguration = historyPluginConfiguration;
    this.esClient = init();
  }

  protected Client init() {
    Client client = null;

    ImmutableSettings.Builder settingsBuilder = ImmutableSettings.builder()
        .put("cluster.name", historyPluginConfiguration.getEsClusterName());

    if (historyPluginConfiguration.isTransportClient()) {
      // sniff for rest of cluster settingsBuilder.put("client.transport.sniff", true);
      addCustomESProperties(settingsBuilder, historyPluginConfiguration.getProperties());

      TransportClient transportClient = new TransportClient(settingsBuilder).addTransportAddress(
          new InetSocketTransportAddress(historyPluginConfiguration.getEsHost(), Integer.parseInt(historyPluginConfiguration.getEsPort())));
      LOGGER.info("Successfully connected to " + transportClient.connectedNodes());
      client = transportClient;
    } else {
      if (esNode == null) {
        // initialize default settings
        settingsBuilder
            .put("node.name", "rocking-camunda-bpm-history")
            .put("node.client", true) // make node a client, so it won't become a master
            .put("node.local", false)
            .put("node.data", false)
            .put("node.http.enabled", true);
//            .put("discovery.zen.ping.multicast.enabled", false)
//            .put("discovery.zen.ping.unicast.hosts", "127.0.0.1:9300");

        addCustomESProperties(settingsBuilder, historyPluginConfiguration.getProperties());

        esNode = NodeBuilder.nodeBuilder()
            .loadConfigSettings(true)
            .settings(settingsBuilder)
            .build();

        if (LOGGER.isLoggable(Level.INFO)) {
          LOGGER.info("Initialized node with settings: " + esNode.settings().getAsMap().toString());
        }

        esNode.start();
      }

      client = esNode.client();
    }

    return client;
  }

  public void dispose() {
    if (esClient != null) {
      esClient.close();
    }
    if (esNode != null) {
      esNode.close();
    }
  }

  public Client get() {
    if (esClient == null) {
      esClient = init();
    }
    return esClient;
  }

  public void set(Client esClient) {
    this.esClient = esClient;
  }

  protected void addCustomESProperties(ImmutableSettings.Builder settingsBuilder, HashMap<String, Object> properties) {
    for (Map.Entry<String, Object> property : properties.entrySet()) {
      if (property.getKey().toLowerCase().startsWith("es.")) {
        settingsBuilder.put(property.getKey().substring(3), property.getValue());
      }
    }
  }

}
