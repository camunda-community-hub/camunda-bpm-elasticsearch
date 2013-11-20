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

package org.camunda.bpm.elasticsearch.util;

import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.common.io.stream.BytesStreamOutput;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

public class ElasticSearchHelper {

  protected static final Logger LOGGER = Logger.getLogger(ElasticSearchHelper.class.getName());

  public static void checkIndex(Client esClient, String indexName) {
    if (!checkIndexExists(esClient, indexName)) {
      if (createIndex(esClient, indexName)) {
        LOGGER.info("Index [" + indexName + "] not found. Creating new index [" + indexName + "]");
      } else {
        throw new RuntimeException("Unable to create index [" + indexName + "] in Elasticsearch.");
      }
    } else {
      LOGGER.info("Index [" + indexName + "] already exists in Elasticsearch.");
    }
  }

  public static boolean createIndex(Client esClient, String indexName) {
    return esClient.admin().indices().prepareCreate(indexName).get().isAcknowledged();
  }

  public static void checkTypeAndMapping(Client esClient, String indexName, String typeName) {
    Map<String, Object> mapping =
        JsonHelper.readJsonFromClasspathAsMap("mapping/" + typeName + ".json");
    checkTypeAndMapping(esClient, indexName, typeName, mapping);
  }

  public static void checkTypeAndMapping(Client esClient, String indexName, String typeName, Map<String, Object> mapping) {
    if (!checkTypeExists(esClient, indexName, typeName)) {
      if (mapping == null) {
        throw new RuntimeException("No mapping provided.");
      }

      if (updateMappingForType(esClient, indexName, typeName, mapping)) {
        LOGGER.info("Created mapping for [" + indexName + "]/[" + typeName + "]");
      } else {
        throw new RuntimeException("Could not define mapping for ["+ indexName +"]/["+ typeName +"]");
      }
    }
  }

  public static boolean checkIndexExists(Client esClient, String indexName) {
    return esClient.admin().indices().prepareExists(indexName).get().isExists();
  }

  public static boolean checkTypeExists(Client esClient, String index, String type) {
    return esClient.admin().indices().prepareExists(index, type).get().isExists();
  }

  public static boolean updateMappingForType(Client esClient, String indexName, String typeName, Map<String, Object> mapping) {
    return esClient.admin().indices().preparePutMapping(indexName).setType(typeName).setSource(mapping).get().isAcknowledged();
  }

  public static String convertRequestToJson(ActionRequest request) throws IOException {
    BytesStreamOutput bytesStreamOutput = new BytesStreamOutput();
    request.writeTo(bytesStreamOutput);

    XContentBuilder builder = XContentFactory.jsonBuilder(bytesStreamOutput);
    builder.prettyPrint();

//    builder.startObject();
//    builder.endObject();
    BytesArray bytesArray = builder.bytes().toBytesArray();
    return new String(bytesArray.array(), bytesArray.arrayOffset(), bytesArray.length());
  }

  public static String convertResponseToJson(ActionResponse response) throws IOException {
    BytesStreamOutput bytesStreamOutput = new BytesStreamOutput();
    response.writeTo(bytesStreamOutput);

    XContentBuilder builder = XContentFactory.jsonBuilder(bytesStreamOutput);
    builder.prettyPrint();

//    builder.startObject();
//    builder.endObject();
    return builder.bytes().toUtf8();
  }


}
