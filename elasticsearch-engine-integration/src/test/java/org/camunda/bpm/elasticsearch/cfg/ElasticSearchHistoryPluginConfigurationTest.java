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

package org.camunda.bpm.elasticsearch.cfg;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.camunda.bpm.elasticsearch.index.ElasticSearchDefaultIndexStrategy;
import org.camunda.bpm.elasticsearch.ElasticSearchHistoryPluginConfiguration;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ElasticSearchHistoryPluginConfigurationTest {

  @Test
  public void writeElasticSearchHistoryPluginConfiguration() throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();

    String config = objectMapper.writeValueAsString(new ElasticSearchHistoryPluginConfiguration());
    System.out.println(config);
  }

  @Test
  public void readElasticSearchHistoryPluginConfiguration() {
    ElasticSearchHistoryPluginConfiguration historyPluginConfiguration = ElasticSearchHistoryPluginConfiguration.readConfigurationFromClasspath();
    assertEquals("camundabpm", historyPluginConfiguration.getIndex());
    assertEquals("processinstance", historyPluginConfiguration.getType());
    assertEquals(ElasticSearchDefaultIndexStrategy.class.getName(), historyPluginConfiguration.getIndexingStrategy());

    assertEquals("localhost", historyPluginConfiguration.getEsHost());
    assertEquals("9300", historyPluginConfiguration.getEsPort());
    assertEquals("camunda_bpm_es_cluster", historyPluginConfiguration.getEsClusterName());
    assertFalse(historyPluginConfiguration.isTransportClient());

    // check properties
    assertEquals("_local_", historyPluginConfiguration.getProperties().get("es.network.host"));
    assertTrue((Boolean) historyPluginConfiguration.getProperties().get("es.node.local"));
  }

}
