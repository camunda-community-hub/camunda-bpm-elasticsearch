package org.camunda.bpm.cockpit.plugin.dashboards.resources;

import org.elasticsearch.search.aggregations.Aggregation;

import java.util.List;
import java.util.Map;

/**
 * Created by hawky4s on 12.08.14.
 */
public class AggregationsResult {

  private Map<String, List<DateHistogramBucketPair>> dateHistogramBuckets;
  private long totalHits;

  public void setDateHistogramBuckets(Map<String, List<DateHistogramBucketPair>> dateHistogramBuckets) {
    this.dateHistogramBuckets = dateHistogramBuckets;
  }

  public Map<String, List<DateHistogramBucketPair>> getDateHistogramBuckets() {
    return dateHistogramBuckets;
  }

  public void setTotalHits(long totalHits) {
    this.totalHits = totalHits;
  }

  public long getTotalHits() {
    return totalHits;
  }
}
