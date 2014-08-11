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

import org.camunda.bpm.elasticsearch.session.ElasticSearchSession;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.history.event.HistoryEvent;

/**
 * {@link ElasticSearchHistoryEventHandler} implementation which adds the event to the current {@link ElasticSearchSession}.
 * The ElasticSearchSession is opened once per command and batches all events fired until the session is closed.
 *
 * @author Christian Lipphardt
 * @author Daniel Meyer
 *
 */
public class ElasticSearchTransactionAwareHistoryEventHandler extends ElasticSearchHistoryEventHandler {

  public void handleEvent(HistoryEvent historyEvent) {

    // get or create current ElasticSearchSession
    ElasticSearchSession elasticSearchSession = Context.getCommandContext()
      .getSession(ElasticSearchSession.class);

    // add event to elastic search session.
    elasticSearchSession.addHistoryEvent(historyEvent);
  }

}
