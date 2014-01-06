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

package org.camunda.bpm.elasticsearch.jackson;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.camunda.bpm.engine.impl.history.event.*;

import java.util.HashMap;
import java.util.Map;

public class JacksonMixInFilterModule extends SimpleModule {

  protected HashMap<Class<? extends HistoryEvent>, Class> customMixInFilters;

  public JacksonMixInFilterModule(HashMap<Class<? extends HistoryEvent>, Class> customMixInFilters) {
    super("ElasticSearchHistoryEntityMixInFilterModule", Version.unknownVersion());
    this.customMixInFilters = customMixInFilters;
  }

  @Override
  public void setupModule(SetupContext context) {
    setupMixIns(context, getDefaultMixInFilters());

    if (customMixInFilters != null) {
      setupMixIns(context, customMixInFilters);
    }

    super.setupModule(context);
  }

  public static String getJsonFilterAnnotationValue(Class clazz) {
    JsonFilter jsonFilterAnnotation = (JsonFilter) clazz.getAnnotation(JsonFilter.class);
    if (jsonFilterAnnotation != null) {
      return jsonFilterAnnotation.value();
    }

    return null;
  }

  protected void setupMixIns(SetupContext context, HashMap<Class<? extends HistoryEvent>, Class> mixInFilters) {
    for (Map.Entry<Class<? extends HistoryEvent>, Class> mixInFilter : mixInFilters.entrySet()) {
      context.setMixInAnnotations(mixInFilter.getKey(), mixInFilter.getValue());
    }
  }

  public static SimpleFilterProvider getCustomFilterProvider() {
    SimpleFilterProvider filterProvider = new SimpleFilterProvider();
    for (Map.Entry<Class<? extends HistoryEvent>, Class> mixInFilter : getDefaultMixInFilters().entrySet()) {
      String jsonFilterId = getJsonFilterAnnotationValue(mixInFilter.getValue());
      if (jsonFilterId != null) {
        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.serializeAllExcept("");
        // TODO: add extension mechanism to declare filter values via ElasticSearchHistoryPluginConfiguration
        filterProvider.addFilter(jsonFilterId, filter);
      }
    }

    return filterProvider;
  }

  public static HashMap<Class<? extends HistoryEvent>, Class> getDefaultMixInFilters() {
    HashMap<Class<? extends HistoryEvent>, Class> mixInFilters = new HashMap<Class<? extends HistoryEvent>, Class>();

    mixInFilters.put(HistoryEvent.class, HistoryEventMixIn.class);
    mixInFilters.put(HistoricScopeInstanceEvent.class, HistoricScopeInstanceEventMixIn.class); // introduced with 7.0.x
    mixInFilters.put(HistoricProcessInstanceEventEntity.class, HistoricProcessInstanceEventMixIn.class);
    mixInFilters.put(HistoricTaskInstanceEventEntity.class, HistoricTaskInstanceEventMixIn.class);
    mixInFilters.put(HistoricActivityInstanceEventEntity.class, HistoricActivityInstanceEventMixIn.class);
    mixInFilters.put(HistoricDetailEventEntity.class, HistoricDetailEventMixIn.class);
    mixInFilters.put(HistoricVariableUpdateEventEntity.class, HistoricVariableUpdateEventMixIn.class);
    mixInFilters.put(HistoricFormPropertyEventEntity.class, HistoricFormPropertyEventMixIn.class);

    return mixInFilters;
  }
}
