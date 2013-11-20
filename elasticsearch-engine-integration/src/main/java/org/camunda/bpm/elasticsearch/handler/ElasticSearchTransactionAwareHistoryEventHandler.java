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

import org.camunda.bpm.elasticsearch.ElasticSearchHistoryPluginConfiguration;
import org.camunda.bpm.elasticsearch.index.ElasticSearchIndexStrategy;
import org.camunda.bpm.engine.impl.cfg.TransactionContext;
import org.camunda.bpm.engine.impl.cfg.TransactionState;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.history.event.HistoryEvent;

import java.util.HashMap;

public class ElasticSearchTransactionAwareHistoryEventHandler extends AbstractElasticSearchHistoryEventHandler {

  protected HashMap<TransactionContext, ElasticSearchEngineTransactionListener> transactionListeners;

  public ElasticSearchTransactionAwareHistoryEventHandler(ElasticSearchHistoryPluginConfiguration historyPluginConfiguration,
                                                          ElasticSearchIndexStrategy indexingStrategy) {
    super(indexingStrategy, historyPluginConfiguration);
    this.transactionListeners = new HashMap<TransactionContext, ElasticSearchEngineTransactionListener>();
  }

  @Override
  public void handleEvent(HistoryEvent historyEvent) {
    registerTransactionListener(historyEvent);
  }

  protected void registerTransactionListener(HistoryEvent historyEvent) {
    TransactionContext transactionContext = Context.getCommandContext()
        .getTransactionContext();

    ElasticSearchEngineTransactionListener transactionListener = transactionListeners.get(transactionContext);

    if (transactionListener == null) {
      transactionListener = new ElasticSearchEngineTransactionListener(this, transactionContext);
      transactionContext.addTransactionListener(TransactionState.COMMITTED, transactionListener);
      transactionListeners.put(transactionContext, transactionListener);
    }

    transactionListener.addHistoryEvent(historyEvent);
  }

  public void removeTransactionListener(TransactionContext transactionContext) {
    if (transactionListeners.containsKey(transactionContext)) {
      transactionListeners.remove(transactionContext);
    }
  }
}
