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

import org.camunda.bpm.elasticsearch.ElasticSearchHistoryEventDispatcher;
import org.camunda.bpm.elasticsearch.jackson.JsonTransformer;
import org.camunda.bpm.engine.impl.history.event.HistoryEvent;
import org.elasticsearch.client.Client;

import java.util.List;

public abstract class ElasticSearchIndexStrategy {

  protected ElasticSearchHistoryEventDispatcher dispatcher = new ElasticSearchHistoryEventDispatcher();
  protected JsonTransformer transformer = new JsonTransformer();
  protected Client esClient;

  public abstract void executeRequest(List<HistoryEvent> historyEvents);

  public abstract void executeRequest(HistoryEvent historyEvent);

  public void setDispatcher(ElasticSearchHistoryEventDispatcher dispatcher) {
    this.dispatcher = dispatcher;
  }

  public void setEsClient(Client esClient) {
    this.esClient = esClient;
  }

}
