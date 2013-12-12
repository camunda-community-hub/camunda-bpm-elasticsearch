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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class JsonHelper {

  private static ObjectMapper objectMapper = new ObjectMapper();

  public static <T> T readJsonFromClasspath(Class<T> clazz, String fileName) {
    InputStream inputStream = null;

    try {
      inputStream = IoUtil.getResourceAsStream(fileName);
      if (inputStream == null) {
        throw new RuntimeException("File '" + fileName + "' not found!");
      }

      T typedJson = objectMapper.readValue(inputStream, clazz);

      return typedJson;
    } catch (IOException e) {
      throw new RuntimeException("Unable to load json [" + fileName + "] from classpath", e);
    } finally {
      IoUtil.closeSilently(inputStream);
    }
  }

  public static Map<String, Object> readJsonFromClasspathAsMap(String fileName) {
    InputStream inputStream = null;

    try {
      inputStream = IoUtil.getResourceAsStream(fileName);
      if (inputStream == null) {
        throw new RuntimeException("File '" + fileName + "' not found!");
      }

      MapType type = TypeFactory.defaultInstance().constructMapType(HashMap.class, String.class, Object.class);
      HashMap<String, Object> mapping = objectMapper.readValue(inputStream, type);

      return mapping;
    } catch (IOException e) {
      throw new RuntimeException("Unable to load json [" + fileName + "] from classpath", e);
    } finally {
      IoUtil.closeSilently(inputStream);
    }
  }

  public static String readJsonFromClasspathAsString(String fileName) {
    InputStream inputStream = null;

    try {
      inputStream = IoUtil.getResourceAsStream(fileName);
      if (inputStream == null) {
        throw new RuntimeException("File '" + fileName + "' not found!");
      }

      Scanner s = new java.util.Scanner(inputStream, "UTF-8").useDelimiter("\\A");
      return s.hasNext() ? s.next() : null;
    } finally {
      IoUtil.closeSilently(inputStream);
    }
  }

}
