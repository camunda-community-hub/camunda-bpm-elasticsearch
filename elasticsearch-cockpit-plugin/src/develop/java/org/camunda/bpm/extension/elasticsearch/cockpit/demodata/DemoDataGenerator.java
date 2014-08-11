/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.bpm.extension.elasticsearch.cockpit.demodata;

import static org.camunda.bpm.engine.authorization.Authorization.ANY;
import static org.camunda.bpm.engine.authorization.Authorization.AUTH_TYPE_GRANT;
import static org.camunda.bpm.engine.authorization.Permissions.ALL;

import org.camunda.bpm.engine.AuthorizationService;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.authorization.Groups;
import org.camunda.bpm.engine.authorization.Resource;
import org.camunda.bpm.engine.authorization.Resources;
import org.camunda.bpm.engine.identity.Group;
import org.camunda.bpm.engine.identity.User;
import org.camunda.bpm.engine.impl.persistence.entity.AuthorizationEntity;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author Daniel Meyer
 *
 */
public class DemoDataGenerator implements InitializingBean {

  protected ProcessEngine processEngine;

  public void afterPropertiesSet() throws Exception {

    System.out.println("Generating demo data");

    // start 10 process instances
    for (int i = 0; i < 10; i++) {
      processEngine.getRuntimeService()
        .startProcessInstanceByKey("generatedFormsQuickstart");
    }

    // ensure admin user exists
    IdentityService identityService = processEngine.getIdentityService();
    User user = identityService.createUserQuery().userId("demo").singleResult();
    if(user == null) {
      User newUser = identityService.newUser("demo");
      newUser.setPassword("demo");
      identityService.saveUser(newUser);
      System.out.println("Created used 'demo', password 'demo'");
      AuthorizationService authorizationService = processEngine.getAuthorizationService();

      // create group
      if(identityService.createGroupQuery().groupId(Groups.CAMUNDA_ADMIN).count() == 0) {
        Group camundaAdminGroup = identityService.newGroup(Groups.CAMUNDA_ADMIN);
        camundaAdminGroup.setName("camunda BPM Administrators");
        camundaAdminGroup.setType(Groups.GROUP_TYPE_SYSTEM);
        identityService.saveGroup(camundaAdminGroup);
      }

      // create ADMIN authorizations on all built-in resources
      for (Resource resource : Resources.values()) {
        if(authorizationService.createAuthorizationQuery().groupIdIn(Groups.CAMUNDA_ADMIN).resourceType(resource).resourceId(ANY).count() == 0) {
          AuthorizationEntity userAdminAuth = new AuthorizationEntity(AUTH_TYPE_GRANT);
          userAdminAuth.setGroupId(Groups.CAMUNDA_ADMIN);
          userAdminAuth.setResource(resource);
          userAdminAuth.setResourceId(ANY);
          userAdminAuth.addPermission(ALL);
          authorizationService.saveAuthorization(userAdminAuth);
        }
      }

      processEngine.getIdentityService()
      .createMembership("demo", Groups.CAMUNDA_ADMIN);
    }
  }



  public void setProcessEngine(ProcessEngine processEngine) {
    this.processEngine = processEngine;
  }

  public ProcessEngine getProcessEngine() {
    return processEngine;
  }

}
