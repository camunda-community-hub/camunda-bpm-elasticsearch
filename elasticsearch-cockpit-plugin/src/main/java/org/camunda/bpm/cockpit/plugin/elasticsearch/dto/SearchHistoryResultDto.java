package org.camunda.bpm.cockpit.plugin.elasticsearch.dto;

import org.elasticsearch.search.SearchHit;

import java.util.List;

public class SearchHistoryResultDto {

  private List<SearchHit> searchHits;
  private List<StatisticsDto> statistics;

  public List<SearchHit> getSearchHits() {
    return searchHits;
  }

  public void setSearchHits(List<SearchHit> searchHits) {
    this.searchHits = searchHits;
  }

  public List<StatisticsDto> getStatistics() {
    return statistics;
  }

  public void setStatistics(List<StatisticsDto> statistics) {
    this.statistics = statistics;
  }
}
