package org.camunda.bpm.cockpit.plugin.elasticsearch.dto;

import java.util.Date;

public class SearchHistoryParamsDto {

  private String queryString;
  private long from;
  private long til;

  public String getQueryString() {
    return queryString;
  }

  public void setQueryString(String queryString) {
    this.queryString = queryString;
  }

  public long getFrom() {
    return from;
  }

  public void setFrom(long from) {
    this.from = from;
  }

  public void setFrom(Date from) {
    this.from = from.getTime();
  }

  public long getTil() {
    return til;
  }

  public void setTil(long til) {
    this.til = til;
  }

  public void setTil(Date til) {
    this.til = til.getTime();
  }

}
