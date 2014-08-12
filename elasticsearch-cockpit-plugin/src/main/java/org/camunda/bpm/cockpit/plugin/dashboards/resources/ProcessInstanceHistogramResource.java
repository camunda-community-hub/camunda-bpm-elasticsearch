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

import org.camunda.bpm.cockpit.plugin.ElasticSearchClientProvider;
import org.camunda.bpm.cockpit.plugin.resource.AbstractCockpitPluginResource;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.filter.Filter;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogram;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramBuilder;

import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import java.util.*;
import java.util.logging.Logger;

import static org.camunda.bpm.elasticsearch.ElasticSearchHistoryPluginConfiguration.ES_DEFAULT_INDEX_NAME_CAMUNDA_BPM;

/**
 * @author Christian Lipphardt
 */
public class ProcessInstanceHistogramResource extends AbstractCockpitPluginResource {

  private final static Logger LOG = Logger.getLogger(ProcessInstanceHistogramResource.class.getName());

  public static final String PATH = "/histogram/processinstance";

  public ProcessInstanceHistogramResource(String engineName) {
    super(engineName);
  }

  @GET
  public AggregationsResult getDateHistogramAggregrations(
      @QueryParam("interval") String interval,
      @QueryParam("timeframe") String timeframe
  ) {

    Client client = ElasticSearchClientProvider.getClient(getProcessEngine());

    DateHistogram.Interval dateInterval = null;
    switch (interval) {
      case "s":
      case "m":
      case "h":
      case "d":
      case "w":
      case "M":
      case "q":
      case "y":
      default:
        dateInterval = DateHistogram.Interval.SECOND;
        break;
    }

    // create buckets based on startTime
    DateHistogramBuilder histogramStartTime = AggregationBuilders.dateHistogram("dateHistogram")
        .minDocCount(0)
        .interval(dateInterval)
        .field("startTime");
    // only get the running process instances
    FilterAggregationBuilder runningPIsAgg = AggregationBuilders.filter("running")
        .filter(FilterBuilders.missingFilter("endTime"));
    runningPIsAgg.subAggregation(histogramStartTime);

    // create buckets based on endTime
    DateHistogramBuilder histogramEndTime = AggregationBuilders.dateHistogram("dateHistogram")
        .minDocCount(0)
        .interval(dateInterval)
        .field("endTime");
    // only get the ended process instances
    FilterAggregationBuilder endedPIsAgg = AggregationBuilders.filter("ended")
        .filter(FilterBuilders.existsFilter("endTime"));
    endedPIsAgg.subAggregation(histogramEndTime);


    SearchRequestBuilder searchRequestBuilder = client.prepareSearch(ES_DEFAULT_INDEX_NAME_CAMUNDA_BPM)
        .setQuery(QueryBuilders.matchAllQuery())
        .addAggregation(runningPIsAgg)
        .addAggregation(endedPIsAgg)
        .setSearchType(SearchType.COUNT);

    System.out.println(searchRequestBuilder);

    SearchResponse searchResponse = searchRequestBuilder.get();

    long totalHits = searchResponse.getHits().getTotalHits();

    Filter running = searchResponse.getAggregations().get("running");
//    long runningTotal = running.getDocCount();

    DateHistogram runningDateHistogram = running.getAggregations().get("dateHistogram");
    List<DateHistogramBucketPair> runningDateHistogramBuckets = parseDateHistogramAggregation(runningDateHistogram);


    Filter ended = searchResponse.getAggregations().get("ended");
//    long endedTotal = ended.getDocCount();

    DateHistogram endedDateHistogram = ended.getAggregations().get("dateHistogram");
    List<DateHistogramBucketPair> endedDateHistogramBuckets = parseDateHistogramAggregation(endedDateHistogram);

    HashMap<String, List<DateHistogramBucketPair>> dateHistogramBucketPairs = new HashMap<>();
    dateHistogramBucketPairs.put("running", runningDateHistogramBuckets);
    dateHistogramBucketPairs.put("ended", endedDateHistogramBuckets);

    AggregationsResult aggregationsResult = new AggregationsResult();
    aggregationsResult.setDateHistogramBuckets(dateHistogramBucketPairs);
    aggregationsResult.setTotalHits(totalHits);

    return aggregationsResult;
  }

  protected List<DateHistogramBucketPair> parseDateHistogramAggregation(DateHistogram dateHistogram) {
    ArrayList<DateHistogramBucketPair> dateHistogramBuckets = new ArrayList<>();

    for (DateHistogram.Bucket bucket : dateHistogram.getBuckets()) {
      dateHistogramBuckets.add(new DateHistogramBucketPair(bucket.getKeyAsNumber(), bucket.getDocCount()));
    }

    return dateHistogramBuckets;
  }

}
