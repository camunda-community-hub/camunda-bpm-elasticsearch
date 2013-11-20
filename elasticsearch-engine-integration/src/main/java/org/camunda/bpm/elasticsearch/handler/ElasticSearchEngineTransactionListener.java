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

package org.camunda.bpm.elasticsearch.handler;

import org.camunda.bpm.elasticsearch.index.ElasticSearchIndexStrategy;
import org.camunda.bpm.engine.impl.cfg.TransactionContext;
import org.camunda.bpm.engine.impl.cfg.TransactionListener;
import org.camunda.bpm.engine.impl.history.event.HistoryEvent;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;

import java.util.ArrayList;
import java.util.List;

public class ElasticSearchEngineTransactionListener implements TransactionListener {

  private final ElasticSearchTransactionAwareHistoryEventHandler historyEventHandler;
  private final TransactionContext transactionContext;
  private final ElasticSearchIndexStrategy indexingStrategy;
  private List<HistoryEvent> historyEvents;

  public ElasticSearchEngineTransactionListener(final ElasticSearchTransactionAwareHistoryEventHandler historyEventHandler, final TransactionContext transactionContext) {
    this.historyEventHandler = historyEventHandler;
    this.transactionContext = transactionContext;
    this.indexingStrategy = historyEventHandler.getIndexingStrategy();
  }

  @Override
  public void execute(CommandContext commandContext) {
    indexingStrategy.executeRequest(historyEvents);
    historyEventHandler.removeTransactionListener(transactionContext);
    historyEvents = null;
  }

  public void setHistoryEvents(List<HistoryEvent> historyEvents) {
    this.historyEvents = historyEvents;
  }

  public void addHistoryEvent(HistoryEvent historyEvent) {
    if (historyEvents == null) {
      historyEvents = new ArrayList<HistoryEvent>();
    }
    historyEvents.add(historyEvent);
  }

}
