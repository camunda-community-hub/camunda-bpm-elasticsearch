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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchAdminClient;
import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchClient;
import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import com.github.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.node.Node;
import org.junit.Rule;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import static org.elasticsearch.client.Requests.clusterStateRequest;
import static org.elasticsearch.client.Requests.refreshRequest;

@RunWith(ElasticsearchRunner.class)
public abstract class AbstractElasticSearchTest {

  protected Logger logger = Logger.getLogger(getClass().getName());

  protected ObjectMapper mapper = new ObjectMapper();

  @ElasticsearchNode(configFile = "config/elasticsearch-test.yml")
  protected Node node;

  @ElasticsearchClient()
  protected Client client;

  @ElasticsearchAdminClient()
  protected AdminClient adminClient;

  @Rule
  public ProcessEngineRule processEngineRule = new ProcessEngineRule();

  protected void ensureIndexRefreshed(String... indices) {
    adminClient.indices().refresh(refreshRequest(indices)).actionGet();
  }

  protected void showMappings(String... indices) throws IOException {
    ClusterStateResponse clusterStateResponse = adminClient.cluster()
        .state(clusterStateRequest()
        .blocks(true)
        .nodes(true)
        .indices(indices))
        .actionGet();

    for (IndexMetaData indexMetaData : clusterStateResponse.getState().getMetaData()) {
      printMapping(indexMetaData.getMappings());
    }

  }

  private void printMapping(Map<String, MappingMetaData> mappedMetaData) {
    for (Map.Entry<String, MappingMetaData> metaDataEntry : mappedMetaData.entrySet()) {
      Map<String, Object> sourceAsMap = null;
      try {
        sourceAsMap = metaDataEntry.getValue().getSourceAsMap();
        logger.info(mapper.writeValueAsString(sourceAsMap));
      } catch (IOException e) {
        // nop
      }
    }
  }
}
