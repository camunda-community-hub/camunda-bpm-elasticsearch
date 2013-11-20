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

package org.camunda.bpm.elasticsearch;

import org.camunda.bpm.engine.impl.history.event.HistoryEvent;

public class ElasticSearchHistoryEventDispatcher {

  public static final String DEFAULT_DISPATCH_INDEX = ElasticSearchHistoryPluginConfiguration.ES_DEFAULT_INDEX_NAME_CAMUNDA_BPM;
  public static final String DEFAULT_DISPATCH_TYPE = ElasticSearchHistoryPluginConfiguration.ES_DEFAULT_TYPE_NAME_CAMUNDA_BPM;


  protected String getDispatchTargetIndexForHistoryEvent(HistoryEvent historyEvent) {
    return null;
  }

  public String getDispatchTargetIndex(HistoryEvent historyEvent) {
    String dispatchTargetTypeForHistoryEvent = getDispatchTargetIndexForHistoryEvent(historyEvent);
    if (dispatchTargetTypeForHistoryEvent != null) {
      return dispatchTargetTypeForHistoryEvent;
    } else {
      return DEFAULT_DISPATCH_INDEX;
    }
  }

  protected String getDispatchTargetTypeForHistoryEvent(HistoryEvent historyEvent) {
    return null;
  }

  public String getDispatchTargetType(HistoryEvent historyEvent) {
    String dispatchTargetForHistoryEvent = getDispatchTargetTypeForHistoryEvent(historyEvent);
    if (dispatchTargetForHistoryEvent != null) {
      return dispatchTargetForHistoryEvent;
    } else {
      return DEFAULT_DISPATCH_TYPE;
    }
  }

}
