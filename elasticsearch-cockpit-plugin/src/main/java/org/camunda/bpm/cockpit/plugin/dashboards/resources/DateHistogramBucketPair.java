package org.camunda.bpm.cockpit.plugin.dashboards.resources;

/**
 * Created by hawky4s on 12.08.14.
 */
public class DateHistogramBucketPair {

  protected Number key;
  protected long docCount;

  public DateHistogramBucketPair(Number key, long docCount) {
    this.key = key;
    this.docCount = docCount;
  }

  public Number getKey() {
    return key;
  }

  public void setKey(Number key) {
    this.key = key;
  }

  public long getDocCount() {
    return docCount;
  }

  public void setDocCount(long docCount) {
    this.docCount = docCount;
  }
}
