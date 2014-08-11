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
package org.camunda.bpm.cockpit.plugin.dashboards.resources;

import static org.camunda.bpm.elasticsearch.ElasticSearchHistoryPluginConfiguration.ES_DEFAULT_INDEX_NAME_CAMUNDA_BPM;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;

import org.camunda.bpm.cockpit.plugin.ElasticSearchClientProvider;
import org.camunda.bpm.cockpit.plugin.resource.AbstractCockpitPluginResource;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.FilteredQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

/**
 * @author Daniel Meyer
 *
 */
public class SearchResource extends AbstractCockpitPluginResource {

  private final static Logger LOG = Logger.getLogger(SearchResource.class.getName());

  public static final String PATH = "/search";

  public SearchResource(String engineName) {
    super(engineName);
  }

  @GET
  public List<String> doSearch(@QueryParam("query") String query) {

    Client client = ElasticSearchClientProvider.getClient(getProcessEngine());

    FilteredQueryBuilder elasticSearchQuery = QueryBuilders.filteredQuery(
      QueryBuilders.matchAllQuery(),
      FilterBuilders.boolFilter().should(
        FilterBuilders.nestedFilter("variables", QueryBuilders.queryString(query).field("variables.*").lenient(true).useDisMax(true)),
        FilterBuilders.nestedFilter("tasks", QueryBuilders.queryString(query).field("tasks.*").lenient(true).useDisMax(true)),
        FilterBuilders.nestedFilter("activities", QueryBuilders.queryString(query).field("activities.*").lenient(true).useDisMax(true))
      )
    );

    SearchRequestBuilder searchRequestBuilder = client.prepareSearch(ES_DEFAULT_INDEX_NAME_CAMUNDA_BPM)
        .setQuery(elasticSearchQuery)
        .setFetchSource(false);

    LOG.info("Running query \n" + searchRequestBuilder);

    SearchResponse searchResponse = searchRequestBuilder.get();
    SearchHits hits = searchResponse.getHits();

    List<String> matchedProcessInstanceIds = new ArrayList<String>();
    for (SearchHit searchHit : hits.getHits()) {
      matchedProcessInstanceIds.add(searchHit.getId());
    }

    return matchedProcessInstanceIds;
  }

}
