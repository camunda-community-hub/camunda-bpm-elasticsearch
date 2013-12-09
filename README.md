camunda BPM - ElasticSearch - [![Build Status](https://travis-ci.org/camunda/camunda-bpm-elasticsearch.png)](https://travis-ci.org/camunda/camunda-bpm-elasticsearch)
===========================

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
Build - see how to build

Requirements:
An ElasticSearch installation reachable from Application Server.

Drop libs into JBoss / Tomcat
Configure process engine plugin for your process engine in application server (JBoss: standalone.xml / Tomcat: bpm-platform.xml)

Components
----------
dc.js
elastic.js

How to contribute
-----------------

How to build
------------
Requires Apache Maven 3 to build.

    mvn clean install

How to test
-----------

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

  [camundabpm] http://www.camunda.org
  [camundabpm-github] https://github.com/camunda/camunda-bpm-platform


TODO
====

  * Initialize ESHistoryEventDispatcher by configuration file. (done)
  * Build mechanism to exclude fields from the history events which should not be serialized by Jackson. (done)
  * Look into transaction participation with the engine -> TransactionListener set into own Session (openSession) / SessionFactory.  (done)
  * Create documentation
    * describe how it works
    * what was the motivation to build it
    * benefit
    * how to install (dependencies) and configure -> http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/setup-configuration.html, http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/modules.html
    * architecture
    * engine-integration
    * cockpit-integration
    * customization/how to extend
    * nested docs vs parent/child -> http://www.elasticsearch.org/blog/managing-relations-inside-elasticsearch/
  * Write tests for
    * mapping
    * performance
    * json filtering (done)
    * queries (possible queries, facets)
  * Extract current query, so it can be easily changed:
     * to a child/parent relationship
     * mapping, single type for pi as parent, types for events as children
  * Support multi-tenancy (append engine-name to index)

Elasticsearch-Cockpit-Plugin
============================
bower install git://github.com/novus/nvd3.git // requires d3.js
bower install elastic.js
bower install dcjs (dc.js)

Elasticsearch-related stuff
===========================
"dynamic_date_formats" : ["yyyy-MM-dd", "dd-MM-yyyy"],
dateOptionalTime (ISO) and yyyy/MM/dd HH:mm:ss Z||yyyy/MM/dd Z
"date_detection" : false, -> stop automatic date type detection


"numeric_detection" : true, -> detect numeric string as number


{
    "person" : {
        "dynamic_templates" : [
            {
                "template_1" : {
                    "match" : "multi*",
                    "mapping" : {
                        "type" : "multi_field",
                        "fields" : {
                            "{name}" : {"type": "{dynamic_type}", "index" : "analyzed"},
                            "org" : {"type": "{dynamic_type}", "index" : "not_analyzed"}
                        }
                    }
                }
            },
            {
                "template_2" : {
                    "match" : "*",
                    "match_mapping_type" : "string",
                    "mapping" : {
                        "type" : "string",
                        "index" : "not_analyzed"
                    }
                }
            }
        ]
    }
}

Queries
=======

variable by name -> (variableName)
variable by value ->
  doubleValue
  longValue
  textValue
  text2Value

variable by time -> (timestamp as range)
variable value missing (value is null)