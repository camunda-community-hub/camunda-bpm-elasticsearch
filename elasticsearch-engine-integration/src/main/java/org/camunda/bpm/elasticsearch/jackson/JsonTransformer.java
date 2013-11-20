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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.SimpleType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.camunda.bpm.engine.impl.history.event.HistoryEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonTransformer {

  protected ObjectMapper mapper = new ObjectMapper();

  public JsonTransformer() {
    configureJsonMapper(null);
  }

  public JsonTransformer(HashMap<Class<? extends HistoryEvent>, Class> mixInFilters) {
    configureJsonMapper(mixInFilters);
  }

  protected void configureJsonMapper(HashMap<Class<? extends HistoryEvent>, Class> mixInFilters) {
    //mapper.configure(WRITE_DATE_KEYS_AS_TIMESTAMPS, true);

    // serialize only non null values
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    // allow filtering of entities
    mapper.registerModule(new JacksonMixInFilterModule(mixInFilters));
    mapper.setFilters(JacksonMixInFilterModule.getCustomFilterProvider());
  }

  public String transformToJson(HistoryEvent historyEvent) throws JsonProcessingException {
    return mapper.writeValueAsString(historyEvent);
  }

  public byte[] transformToBytes(HistoryEvent historyEvent) throws JsonProcessingException {
    return mapper.writeValueAsBytes(historyEvent);
  }

  public Map<String, Object> transformJsonToMap(String json) throws IOException {
    MapType mapType = TypeFactory.defaultInstance().constructMapType(HashMap.class, String.class, Object.class);
    return mapper.readValue(json, mapType);
  }

  public List<Map<String, Object>> transformJsonToList(String json) throws IOException {
    JavaType javaType = MapType.construct(HashMap.class, SimpleType.construct(String.class), SimpleType.construct(Object.class));

    CollectionType listType = TypeFactory.defaultInstance().constructCollectionType(ArrayList.class, javaType);
    return mapper.readValue(json, listType);
  }

}