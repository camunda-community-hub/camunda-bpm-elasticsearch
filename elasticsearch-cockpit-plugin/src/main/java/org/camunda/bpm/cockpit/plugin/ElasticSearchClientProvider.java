/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.bpm.cockpit.plugin;

import java.util.List;

import org.camunda.bpm.elasticsearch.ElasticSearchClient;
import org.camunda.bpm.elasticsearch.ElasticSearchHistoryPlugin;
import org.camunda.bpm.elasticsearch.ElasticSearchHistoryPluginConfiguration;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.impl.ProcessEngineImpl;
import org.camunda.bpm.engine.impl.cfg.ProcessEnginePlugin;
import org.elasticsearch.client.Client;

/**
 * @author Daniel Meyer
 *
 */
public class ElasticSearchClientProvider {

  protected static ElasticSearchClient cachedClient = null;

  public static Client getClient(ProcessEngine processEngine) {

    if(cachedClient == null) {
      createClient(processEngine);
    }

      return cachedClient.get();
  }

  protected static void createClient(ProcessEngine processEngine) {
    List<ProcessEnginePlugin> processEnginePlugins = ((ProcessEngineImpl) processEngine).getProcessEngineConfiguration()
      .getProcessEnginePlugins();

    // check whether process enigne has elastic search plugin configured
    for (ProcessEnginePlugin processEnginePlugin : processEnginePlugins) {
      if (processEnginePlugin instanceof ElasticSearchHistoryPlugin) {
        ElasticSearchHistoryPlugin historyPlugin = (ElasticSearchHistoryPlugin) processEnginePlugin;
        cachedClient = historyPlugin.getElasticSearchClient();
        break;
      }
    }

    if(cachedClient == null) {
      // create new client from classpath configuration
      ElasticSearchHistoryPluginConfiguration elasticSearchPluginConfiguration = ElasticSearchHistoryPluginConfiguration.readConfigurationFromClasspath();
      cachedClient = new ElasticSearchClient(elasticSearchPluginConfiguration);
    }
  }

}
