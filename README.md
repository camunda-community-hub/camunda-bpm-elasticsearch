camunda BPM - ElasticSearch extension
===========================

[![Build Status](https://drone.io/github.com/camunda/camunda-bpm-elasticsearch/status.png)](https://drone.io/github.com/camunda/camunda-bpm-elasticsearch/latest)

camunda-bpm-elasticsearch combines the camunda-bpm platform with the powerful search capabilities of ElasticSearch.
It comes with a process engine plugin which indexes all generated history events through ElasticSearch.
To use the indexed data, a cockpit plugin is also provided to allow querying and retrieval of process instances through ElasticSearch.


Components (Modules)
--------------------
<table>
  <tr>
    <th>Module</th><th>Description</th>
  </tr>
  <tr>
    <td>elasticsearch-cockpit-plugin</td><td>Integration of ElasticSearch with camunda BPM cockpit as a cockpit plugin.</td>
  </tr>
  <tr>
    <td>elasticsearch-engine-integration</td><td>Integration of ElasticSearch with camunda BPM engine as a process engine plugin.</td>
  </tr>
  <tr>
    <td>elasticsearch-jboss-module</td><td>Produces in its target directory a folder named modules with all required libraries to use with JBoss 7.x.</td>
  </tr>
  <tr>
      <td>elasticsearch-lib-module</td><td>Produces in its target directory a folder with all required libraries to drop into Tomcat's lib directory.</td>
    </tr>
</table>

How to use
----------
Requirements:
An ElasticSearch installation reachable from Application Server.
Dynamic scripting enabled through script.disable_dynamic: false (required since elasticsearch 1.2)

Drop libs into JBoss / Tomcat
Configure process engine plugin for your process engine in application server (JBoss: standalone.xml / Tomcat: bpm-platform.xml)

License
-------

<pre>
This software is licensed under the Apache 2 license, quoted below.

Copyright 2013 Christian Lipphardt and camunda services GmbH <http://www.camunda.com>

Licensed under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License. You may obtain a copy of
the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License for the specific language governing permissions and limitations under
the License.
</pre>
