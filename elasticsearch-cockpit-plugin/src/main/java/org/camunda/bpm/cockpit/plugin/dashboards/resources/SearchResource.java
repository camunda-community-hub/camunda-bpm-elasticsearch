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
package org.camunda.bpm.cockpit.plugin.dashboards.resources;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;

import org.camunda.bpm.cockpit.plugin.resource.AbstractCockpitPluginResource;
import org.camunda.bpm.engine.rest.dto.runtime.ProcessInstanceDto;

/**
 * @author Daniel Meyer
 *
 */
public class SearchResource extends AbstractCockpitPluginResource {

  public static final String PATH = "/search";

  public SearchResource(String engineName) {
    super(engineName);
  }

  @GET
  public List<ProcessInstanceDto> doSearch(@QueryParam("query") String query) {
    // TODO: search :)
    return new ArrayList<ProcessInstanceDto>();
  }

}
