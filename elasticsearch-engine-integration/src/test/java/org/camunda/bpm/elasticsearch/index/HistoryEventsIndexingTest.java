/*
 * Copyright 2013 - Christian Lipphardt and camunda services GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.camunda.bpm.elasticsearch.index;

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

public class HistoryEventsIndexingTest extends AbstractElasticSearchTest {

  @Test
  public void testIndexingSingleInvoice() throws IOException {
    HashMap<String,ProcessDataContainer> processesById = TestDataGenerator.startInvoiceProcess(processEngineRule.getProcessEngine(), 1);

    String[] pids = processesById.keySet().toArray(new String[0]);

    // elasticsearch //////////////////////////////

    flushAndRefresh();

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
  }

}
