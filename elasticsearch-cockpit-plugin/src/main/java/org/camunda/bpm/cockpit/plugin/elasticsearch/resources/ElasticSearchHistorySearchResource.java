package org.camunda.bpm.cockpit.plugin.elasticsearch.resources;

import org.camunda.bpm.cockpit.plugin.elasticsearch.dto.SearchHistoryParamsDto;
import org.camunda.bpm.cockpit.plugin.resource.AbstractPluginResource;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.FilteredQueryBuilder;
import org.elasticsearch.index.query.NestedFilterBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeFilterBuilder;
import org.elasticsearch.search.facet.FacetBuilders;

import javax.ws.rs.POST;

public class ElasticSearchHistorySearchResource extends AbstractPluginResource {

  private TransportClient esClient;

  public ElasticSearchHistorySearchResource(String engineName) {
    super(engineName);
  }

  public ElasticSearchHistorySearchResource(TransportClient esClient, String engineName) {
    super(engineName);
    this.esClient = esClient;
  }

  @POST
  public SearchResponse searchHistory(SearchHistoryParamsDto searchHistoryParams) {
    String queryValue = searchHistoryParams.getQueryString();

    SearchRequestBuilder srb = new SearchRequestBuilder(esClient)
        .setIndices()
        .setTypes();

    NestedFilterBuilder activities = FilterBuilders.nestedFilter(
        "activities",
        QueryBuilders.queryString(queryValue).field("activities.*").useDisMax(true).lenient(true)
    );
    NestedFilterBuilder tasks = FilterBuilders.nestedFilter(
        "tasks",
        QueryBuilders.queryString(queryValue).field("tasks.*").useDisMax(true).lenient(true)
    );
    NestedFilterBuilder variables = FilterBuilders.nestedFilter(
        "variables",
        QueryBuilders.queryString(queryValue).field("variables.*").useDisMax(true).lenient(true)
    );

    FilteredQueryBuilder queryBuilder = QueryBuilders.filteredQuery(
        QueryBuilders.matchAllQuery(),
        FilterBuilders.boolFilter().should(activities, tasks, variables)
    );

    RangeFilterBuilder rangeFilterBuilder = FilterBuilders.rangeFilter("time-window")
        .gte(searchHistoryParams.getFrom())
        .lte(searchHistoryParams.getTil());

    srb.setQuery(queryBuilder);

    FacetBuilders.dateHistogramFacet("pi-per-day")
        .keyField("startTime")
        .interval("day");

    // facet for running instances


    // facet for ended instances

    // facet for duration - need statistical script probably

    //srb.addFacet()
    //    .addSort()
    //    .setSize(100);

//    if (DEBUG) {
//      srb.setExplain(true);
//      System.out.println(srb.toString());
//    }

    SearchResponse searchResponse = srb.get();

    return searchResponse;
  }

}
