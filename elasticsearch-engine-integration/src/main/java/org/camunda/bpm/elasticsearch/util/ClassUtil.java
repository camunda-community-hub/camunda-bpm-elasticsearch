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

import org.camunda.bpm.engine.ClassLoadingException;
import org.camunda.bpm.engine.ProcessEngineException;
import org.camunda.bpm.engine.impl.util.ClassLoaderUtil;

public class ClassUtil {

  public static <T> T createInstance(Class<? extends T> clazz) {
    try {
      return clazz.newInstance();

    } catch (InstantiationException e) {
      throw new ProcessEngineException("Could not instantiate class", e);
    } catch (IllegalAccessException e) {
      throw new ProcessEngineException("IllegalAccessException while instantiating class", e);
    }
  }

  @SuppressWarnings("unchecked")
  public static <T> Class<? extends T> loadClass(String className, ClassLoader customClassloader, Class<T> clazz) {
    try {
      if(customClassloader != null) {
        return (Class<? extends T>) customClassloader.loadClass(className);
      }else {
        return (Class<? extends T>) ClassUtil.loadClass(className);
      }

    } catch (ClassNotFoundException e) {
      throw new ProcessEngineException("Could not load configuration class", e);

    } catch (ClassCastException e) {
      throw new ProcessEngineException("Custom class of wrong type. Must extend "+clazz.getName(), e);

    }
  }

  public static Class<?> loadClass(String className) {
    Class<?> clazz = null;
    ClassLoader classLoader = null;

    Throwable throwable = null;

    try {
      ClassLoader contextClassloader = ClassLoaderUtil.getContextClassloader();
      if(contextClassloader != null) {
        clazz = Class.forName(className, true, contextClassloader);
      }
    } catch(Throwable t) {
      if(throwable == null) {
        throwable = t;
      }
    }
    if(clazz == null) {
      try {
        clazz = Class.forName(className, true, ClassUtil.class.getClassLoader());
      } catch(Throwable t) {
        if(throwable == null) {
          throwable = t;
        }
      }
    }
    if(clazz == null) {
      throw new ClassLoadingException(className, throwable);
    }
    return clazz;
  }

}
