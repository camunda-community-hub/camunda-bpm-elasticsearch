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

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.impl.test.ProcessEngineAssert;
import org.camunda.bpm.engine.impl.util.ClockUtil;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.runtime.VariableInstance;
import org.camunda.bpm.engine.task.Task;
import org.elasticsearch.common.base.Charsets;
import org.junit.Assert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;

public class TestDataGenerator {

  private static final Logger LOGGER = Logger.getLogger(TestDataGenerator.class.getName());
  private static final Random RANDOM = new Random();

  public static String getRandomString() {
    URL names = TestDataGenerator.class.getClassLoader().getResource("data/names.txt");
    return randomString(names);
  }

  public static Double getRandomDouble() {
    return RANDOM.nextDouble() * 1000;
  }

  public static Long getRandomLong() {
    return RANDOM.nextLong();
  }

  public static String randomString(URL nodeNames) {
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new InputStreamReader(nodeNames.openStream(), Charsets.UTF_8));
      int numberOfNames = 0;
      while (reader.readLine() != null) {
        numberOfNames++;
      }
      reader.close();
      reader = new BufferedReader(new InputStreamReader(nodeNames.openStream(), Charsets.UTF_8));
      int number = ((new Random().nextInt(numberOfNames)) % numberOfNames);
      for (int i = 0; i < number; i++) {
        reader.readLine();
      }
      return reader.readLine();
    } catch (IOException e) {
      return null;
    } finally {
      try {
        if (reader != null) {
          reader.close();
        }
      } catch (IOException e) {
        // ignore this exception
      }
    }
  }

  public static HashMap<String, ProcessDataContainer> startInvoiceProcess(ProcessEngine processEngine, final int numberOfInstances) {
    return startInvoiceProcess(processEngine, numberOfInstances, false);
  }

  public static HashMap<String, ProcessDataContainer> startInvoiceProcess(ProcessEngine processEngine, final int numberOfInstances, boolean addRandomTimeInterval) {
    RepositoryService repositoryService = processEngine.getRepositoryService();
    RuntimeService runtimeService = processEngine.getRuntimeService();
    TaskService taskService = processEngine.getTaskService();

    Deployment deployment = repositoryService.createDeployment().addClasspathResource("invoice.bpmn").deploy();
    Assert.assertNotNull(repositoryService.createDeploymentQuery().deploymentId(deployment.getId()).singleResult());

    LOGGER.info("Creating " + numberOfInstances + " instances of 'invoice.bpmn' process.");


    HashMap<String, ProcessDataContainer> variablesByProcessIds = new HashMap<String, ProcessDataContainer>(numberOfInstances);

    for (int i = 0; i < numberOfInstances; i++) {
      if (addRandomTimeInterval) {
        ClockUtil.setCurrentTime(new Date(ClockUtil.getCurrentTime().getTime() + getRandomLong()));
      }

      HashMap<String, Object> variables = new HashMap<String, Object>();
      variables.put(TestDataGenerator.getRandomString(), TestDataGenerator.getRandomString());
      variables.put("long", TestDataGenerator.getRandomLong());
      variables.put("double", TestDataGenerator.getRandomDouble());

      ProcessInstance pi = runtimeService.startProcessInstanceByKey("invoice", variables);
      Assert.assertNotNull(pi);

      List<Task> tasks = taskService.createTaskQuery().processInstanceId(pi.getId()).list();

      assertEquals(1, tasks.size());
      assertEquals("assignApprover", tasks.get(0).getTaskDefinitionKey());

      variables.clear();
      String approver = TestDataGenerator.getRandomString();
      variables.put("approver", approver);
      taskService.complete(tasks.get(0).getId(), variables);

      tasks = taskService.createTaskQuery().processInstanceId(pi.getId()).list();

      assertEquals(1, tasks.size());
      assertEquals("approveInvoice", tasks.get(0).getTaskDefinitionKey());
      assertEquals(approver, tasks.get(0).getAssignee());

      variables.clear();
      variables.put("approved", Boolean.TRUE);
      taskService.complete(tasks.get(0).getId(), variables);

      tasks = taskService.createTaskQuery().processInstanceId(pi.getId()).list();

      // retrieve all variables
      List<VariableInstance> variableInstances = runtimeService.createVariableInstanceQuery().processInstanceIdIn(pi.getProcessInstanceId()).list();
      variablesByProcessIds.put(pi.getProcessInstanceId(), new ProcessDataContainer(pi.getProcessInstanceId(), pi.getBusinessKey(), variableInstances));

      assertEquals(1, tasks.size());
      assertEquals("prepareBankTransfer", tasks.get(0).getTaskDefinitionKey());
      taskService.complete(tasks.get(0).getId());

      ProcessEngineAssert.assertProcessEnded(processEngine, pi.getId());
    }

    LOGGER.info("Created " + numberOfInstances + " instances of 'invoice.bpmn' process.");

    return variablesByProcessIds;
  }

  private TestDataGenerator() {}
}
