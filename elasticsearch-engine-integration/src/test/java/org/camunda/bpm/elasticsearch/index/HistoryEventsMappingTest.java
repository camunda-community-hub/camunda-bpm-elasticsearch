package org.camunda.bpm.elasticsearch.index;

import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchIndex;
import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchMapping;
import org.camunda.bpm.elasticsearch.AbstractElasticSearchTest;
import org.camunda.bpm.elasticsearch.ProcessDataContainer;
import org.camunda.bpm.elasticsearch.TestDataGenerator;
import org.camunda.bpm.elasticsearch.util.IoUtil;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.FilteredQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.camunda.bpm.elasticsearch.ElasticSearchHistoryPluginConfiguration.ES_DEFAULT_INDEX_NAME_CAMUNDA_BPM;
import static org.camunda.bpm.elasticsearch.ElasticSearchHistoryPluginConfiguration.ES_DEFAULT_TYPE_NAME_CAMUNDA_BPM;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class HistoryEventsMappingTest extends AbstractElasticSearchTest {

  @Test
//  @ElasticsearchIndex(
//      indexName = ES_DEFAULT_INDEX_NAME_CAMUNDA_BPM,
//      cleanAfter = true,
//      mappings = @ElasticsearchMapping(typeName = ES_DEFAULT_TYPE_NAME_CAMUNDA_BPM, properties = {
//          @Elasticsearch
//      }))
  public void testIndexingSingleInvoice() throws IOException {
    HashMap<String,ProcessDataContainer> processesById = TestDataGenerator.startInvoiceProcess(processEngineRule.getProcessEngine(), 1);

    String[] pids = processesById.keySet().toArray(new String[0]);

    // elasticsearch //////////////////////////////

    flushAndRefresh();

    showMappings(ES_DEFAULT_INDEX_NAME_CAMUNDA_BPM);

    FilteredQueryBuilder query = QueryBuilders.filteredQuery(QueryBuilders.matchAllQuery(), FilterBuilders.termFilter("processInstanceId", pids[0]));

    SearchRequestBuilder searchRequestBuilder = client.prepareSearch(ES_DEFAULT_INDEX_NAME_CAMUNDA_BPM)
        .setQuery(query);

    IoUtil.writeToFile(searchRequestBuilder.toString(), this.getClass().getSimpleName() + ".json", true);

    SearchResponse searchResponse = searchRequestBuilder.get();
    SearchHits hits = searchResponse.getHits();
    assertEquals(1, hits.totalHits());


    SearchHit hit = hits.getAt(0);
    assertEquals(pids[0], hit.getId());
    assertEquals(ES_DEFAULT_TYPE_NAME_CAMUNDA_BPM, hit.getType());

    Map<String,Object> source = hit.getSource();
    assertNotNull(source.get("startTime"));
    assertNotNull(source.get("endTime"));
    ArrayList variables = (ArrayList) source.get("variables");
    assertEquals(5, variables.size());
    ArrayList tasks = (ArrayList) source.get("tasks");
    assertEquals(9, tasks.size());
    ArrayList activities = (ArrayList) source.get("activities");
    assertEquals(19, activities.size());

    logger.info(searchResponse.toString());

//    for (SearchHit searchHit : searchResponse.getHits()) {
//      logger.info(searchHit.sourceAsString());
//    }

    showMappings(ES_DEFAULT_INDEX_NAME_CAMUNDA_BPM);

    assertMappings(ES_DEFAULT_INDEX_NAME_CAMUNDA_BPM);
    // TODO: write assertions for mapping
  }

  protected void assertMappings(String esDefaultIndexNameCamundaBpm) {

  }

}
