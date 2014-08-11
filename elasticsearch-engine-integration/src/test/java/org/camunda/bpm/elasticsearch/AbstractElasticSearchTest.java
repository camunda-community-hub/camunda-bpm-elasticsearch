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
import org.camunda.bpm.elasticsearch.util.ElasticSearchHelper;
import org.camunda.bpm.engine.impl.ProcessEngineImpl;
import org.camunda.bpm.engine.impl.cfg.ProcessEnginePlugin;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.elasticsearch.action.ShardOperationFailedException;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthStatus;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.action.admin.indices.flush.FlushResponse;
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse;
import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.hppc.cursors.ObjectCursor;
import org.elasticsearch.node.Node;
import org.elasticsearch.rest.RestStatus;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static org.elasticsearch.client.Requests.clusterStateRequest;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(ElasticsearchRunner.class)
public abstract class AbstractElasticSearchTest {

  protected Logger logger = Logger.getLogger(AbstractElasticSearchTest.class.getName());

  protected ObjectMapper mapper = new ObjectMapper();

  @ElasticsearchNode(configFile = "config/elasticsearch-test.yml")
  protected Node elasticsearchNode;
  @ElasticsearchClient
  protected Client client;
  @ElasticsearchAdminClient
  protected AdminClient adminClient;

  @Rule
  public ProcessEngineRule processEngineRule = new ProcessEngineRule();

  @Before
  public void initialize() {
    ElasticSearchHistoryPluginConfiguration historyPluginConfiguration = ElasticSearchHistoryPluginConfiguration.readConfigurationFromClasspath();
    ElasticSearchHelper.checkIndex(client, historyPluginConfiguration.getIndex());
    ElasticSearchHelper.checkTypeAndMapping(client, historyPluginConfiguration.getIndex(), historyPluginConfiguration.getType());

    List<ProcessEnginePlugin> processEnginePlugins = ((ProcessEngineImpl) processEngineRule.getProcessEngine())
        .getProcessEngineConfiguration()
        .getProcessEnginePlugins();
    for (ProcessEnginePlugin processEnginePlugin : processEnginePlugins) {
      if (processEnginePlugin instanceof ElasticSearchHistoryPlugin) {
        ElasticSearchHistoryPlugin plugin = (ElasticSearchHistoryPlugin) processEnginePlugin;
        plugin.setElasticSearchClient(client);
      }
    }
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

  protected void printMapping(ImmutableOpenMap<String, MappingMetaData> mappedMetaData) {
    for (ObjectCursor<MappingMetaData> metaDataEntry : mappedMetaData.values()) {
      try {
        Map<String, Object> sourceAsMap = metaDataEntry.value.getSourceAsMap();
        logger.info(mapper.writeValueAsString(sourceAsMap));
      } catch (IOException e) {
        // nop
      }
    }
  }

  /**
   * Waits for relocations and refreshes all indices in the cluster.
   *
   * @see #waitForRelocation()
   */
  protected final RefreshResponse refresh() {
    waitForRelocation();
    // TODO RANDOMIZE with flush?
    RefreshResponse actionGet = adminClient.indices().prepareRefresh().execute().actionGet();
//    assertNoFailures(actionGet);
    return actionGet;
  }

  /**
   * Flushes and refreshes all indices in the cluster
   */
  protected final void flushAndRefresh() {
    flush(true);
    refresh();
  }

  /**
   * Flushes all indices in the cluster
   */
  protected final FlushResponse flush() {
    return flush(true);
  }

  private FlushResponse flush(boolean ignoreNotAllowed) {
    waitForRelocation();
    FlushResponse actionGet = adminClient.indices().prepareFlush().setForce(true).setFull(true).execute().actionGet();
    if (ignoreNotAllowed) {
      for (ShardOperationFailedException failure : actionGet.getShardFailures()) {
//        assertThat("unexpected flush failure " + failure.reason(), failure.status(), equalTo(RestStatus.SERVICE_UNAVAILABLE));
      }
    } else {
//      assertNoFailures(actionGet);
    }
    return actionGet;
  }

  /**
   * Waits for all relocating shards to become active using the cluster health API.
   */
  public ClusterHealthStatus waitForRelocation() {
    return waitForRelocation(null);
  }

  /**
   * Waits for all relocating shards to become active and the cluster has reached the given health status
   * using the cluster health API.
   */
  public ClusterHealthStatus waitForRelocation(ClusterHealthStatus status) {
    ClusterHealthRequest request = Requests.clusterHealthRequest().waitForRelocatingShards(0);
    if (status != null) {
      request.waitForStatus(status);
    }
    ClusterHealthResponse actionGet = adminClient.cluster()
        .health(request).actionGet();
    if (actionGet.isTimedOut()) {
//      logger.info("waitForRelocation timed out (status={}), cluster state:\n{}\n{}", status, adminClient.cluster().prepareState().get().getState().prettyPrint(), adminClient.cluster().preparePendingClusterTasks().get().prettyPrint());
      assertThat("timed out waiting for relocation", actionGet.isTimedOut(), equalTo(false));
    }
    if (status != null) {
      assertThat(actionGet.getStatus(), equalTo(status));
    }
    return actionGet.getStatus();
  }
}
