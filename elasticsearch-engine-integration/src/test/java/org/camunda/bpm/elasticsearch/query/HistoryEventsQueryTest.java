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

package org.camunda.bpm.elasticsearch.query;

import org.camunda.bpm.elasticsearch.AbstractElasticSearchTest;
import org.camunda.bpm.elasticsearch.ProcessDataContainer;
import org.camunda.bpm.elasticsearch.TestDataGenerator;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;

public class HistoryEventsQueryTest extends AbstractElasticSearchTest {

  @Ignore
  @Test
  public void queryShouldFindProcessInstanceViaVariableValue() {
    HashMap<String,ProcessDataContainer> stringProcessDataContainerHashMap = TestDataGenerator.startInvoiceProcess(processEngineRule.getProcessEngine(), 10);

//    flushAndRefresh();

//    QueryBuilders.boolQuery().must(QueryBuilders.nestedQuery(""))
//    SearchRequestBuilder srb = client.prepareSearch().setQuery();
  }

}
