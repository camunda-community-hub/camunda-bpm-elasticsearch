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

import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class IoUtil {

  public static final Charset DEFAULT_UTF_8_CHARSET = Charset.forName("UTF-8");

  public static void writeToFile(String content, String fileName) {
    writeToFile(content.getBytes(DEFAULT_UTF_8_CHARSET), fileName, true);
  }

  public static void writeToFile(String content, String fileName, boolean createFile) {
    writeToFile(content.getBytes(DEFAULT_UTF_8_CHARSET), fileName, createFile);
  }

  public static void writeToFile(byte[] content, String fileName, boolean createFile) {
    File file = new File(fileName);
    try {
      if (createFile) {
        Files.createParentDirs(file);
        file.createNewFile();
      }

      Files.write(content, file);
    } catch (IOException e) {
      // nop
    }
  }

  public static InputStream getResourceAsStream(String name) {
    InputStream resourceStream = null;

      // Try the current Thread context classloader
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    resourceStream = classLoader.getResourceAsStream(name);
    if(resourceStream == null) {
      // Finally, try the classloader for this class
      classLoader = IoUtil.class.getClassLoader();
      resourceStream = classLoader.getResourceAsStream(name);
    }

    return resourceStream;
  }

  /**
   * Closes the given stream. The same as calling {@link InputStream#close()}, but
   * errors while closing are silently ignored.
   */
  public static void closeSilently(InputStream inputStream) {
    try {
      if(inputStream != null) {
        inputStream.close();
      }
    } catch(IOException ignore) {
      // Exception is silently ignored
    }
  }

  /**
   * Closes the given stream. The same as calling {@link java.io.OutputStream#close()}, but
   * errors while closing are silently ignored.
   */
  public static void closeSilently(OutputStream outputStream) {
    try {
      if(outputStream != null) {
        outputStream.close();
      }
    } catch(IOException ignore) {
      // Exception is silently ignored
    }
  }
}
