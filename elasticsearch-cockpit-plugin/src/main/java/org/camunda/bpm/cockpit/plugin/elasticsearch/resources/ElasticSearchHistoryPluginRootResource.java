package org.camunda.bpm.cockpit.plugin.elasticsearch.resources;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.camunda.bpm.cockpit.plugin.elasticsearch.ElasticSearchHistoryPlugin;
import org.camunda.bpm.cockpit.plugin.resource.AbstractPluginRootResource;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

@Path("plugin/" + ElasticSearchHistoryPlugin.ID)
public class ElasticSearchHistoryPluginRootResource extends AbstractPluginRootResource {

  private Client esClient;

  public ElasticSearchHistoryPluginRootResource(String pluginName) {
    super(ElasticSearchHistoryPlugin.ID);

    initElasticSearchClient();
  }

  private void initElasticSearchClient() {

    // reuse elasticsearchconfiguration

    if (esClient == null) {
      Settings settings = ImmutableSettings
          .settingsBuilder()
          .put("cluster.name", "elasticsearch")
          .build();
      esClient = new TransportClient(settings)
          .addTransportAddress(new InetSocketTransportAddress("localhost", 9300));
    }
  }

  @Path("{engineName}/history/search")
  public ElasticSearchHistorySearchResource getElasticSearchHistorySearchResource(@PathParam("engineName") String engineName) {
    new ElasticSearchHistorySearchResource(esClient, engineName);
    return subResource(new ElasticSearchHistorySearchResource(engineName), engineName);
  }

}
