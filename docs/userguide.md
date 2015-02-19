# Camunda BPM Elasticsearch User Guide

## Overview

The Camunda BPM Elasticsearch extension is a community extension which allows you to use
Elasticsearch as data store for Camunda history and audit data.

### Giving it a quick Try

If you want to have a quick look at this project, you can start a running instance of Camunda BPM
with Elasticsearch in the following way:

* Cone the project using git: `git clone https://github.com/camunda/camunda-bpm-elasticsearch.git`
* Build the project using maven: `mvn clean install -DskipTests`
* Start the camunda Webapp from the commandline: 

```bash
cd elasticsearch-cockpit-plugin
mvn clean jetty:run -Pdevelop
```
* go to [http://localhost:8080/camunda](http://localhost:8080/camunda)
* Login with `demo:demo`

In Cockpit you will see a couple of plugins which allow you to search through the process engine
data using Elasticsearch.

### How it works

The Camunda process engine produces a continuous event stream of auditing data (Read Userguide:
[History][userguide:history_event_stream]). This event stream contains events about process instances
being started, tasks being completed, variables being modified and so forth (check the Camunda BPM
Userguide for a complete list of supported events).
The Camunda Elasticsearch Extension implements the `HistoryEventHandler` SPI and stores all history
events in Elasticsearch. Once the history data is stored in Elasticsearch, it can be queried in a
flexible way.

### Components

The Camunda Elasticsearch Extension is composed of two modules: a process engine plugin and a
Cockpit plugin.

#### The Process Engine Plugin

The process engine plugin implements the `HistoryEventHandler` SPI and stores camunda history events
in Elasticsearch.

#### The Cockpit Plugin

The Cockpit plugin enhances Camunda BPM Cockpit and provides different view for accessing the
History Data stored in Elasticsearch.

##### The Quick Search View

The Quick Seach View is a simple seach bar which allows full text search on all stored events.

![Quick Search Screenshot][quick-search-screenshot]

##### The Activity Monitoring View

The Activity Monitoring View shows an histogram with the number of started and completed process
instances over time.

## Installation & Configuration

### Supported Camunda BPM Version

Currently camunda BPM 7.2.0 is required.

### Installing the Process Engine Integration

The process engine integration needs to be added as [Process Engine Plugin][process-engine-plugin]
to Camunda Process Engine. The setup depends on whether you use a *shared* or an *embedded* process
engine.

#### Installation with Embedded Process Engine

In order to use the Elasticsearch Module with an embedded process engine you need to add it to the
process engine classpath and configure the process engine to use the plugin.

##### Adding the Elasticsearch plugin to the classpath

If your application uses Apache Maven, you need to add the following dependencies to your
application:

```xml
<dependencies>
  <dependency>
    <groupId>org.camunda.bpm.extension.elasticsearch</groupId>
    <artifactId>elasticsearch-engine-integration</artifactId>
    <version>1.0.0-SNAPSHOT</version>
  </dependency>
  <dependency>
    <groupId>org.elasticsearch</groupId>
    <artifactId>elasticsearch</artifactId>
    <version>1.3.1</version>
  </dependency>
</dependencies>
```

##### Spring based Configuration

If you bootstrap your process engine using the Spring Framework, you can configure the Elasticsearch plugin using Spring:

```xml
<bean id="processEngineConfiguration" class="org.camunda.bpm.engine.spring.SpringProcessEngineConfiguration">
  ...
  <property name="processEnginePlugins">
    <list>
      <ref bean="elasticSearchPlugin" />
    </list>
  </property>
</bean>

<bean id="elasticSearchPlugin" class="org.camunda.bpm.elasticsearch.ElasticSearchHistoryPlugin">
</bean>
```

#### Installation with Shared Process Engine

If you use a shared process engine, you need to add camunda Elasticsearch JAR files and all
required Elasticsearch libraries to the process engine classpath and configure the plugin in
`bpm-platform.xml` or `standalone.xml` (JBoss).

##### Installation in JBoss Application Server Distribution

In order to install the Elasticsearch extension on JBoss Application Server with Camunda BPM
subsystem, the following steps are necessary:

* Download a pre-packaged distribution from [camunda.org](http://camunda.org/download/).
* Unpack the Camunda distribution into a folder of your choice.
* Clone the [camunda-bpm-elasticsearch][repository] repository using Git.
* Build the repository by typing `mvn clean install`
* Copy the modules from `elasticsearch-jboss-module/target/modules/` to the modules folder of your
   jboss distribution.
* Inside the modules folder of your JBoss Application Server, edit the file 
  `modules/org/camunda/bpm/jboss/camunda-jboss-subsystem/main/module.xm` and add a
  module dependency to the elasticsearch module:

```xml
<module xmlns="urn:jboss:module:1.0" name="org.camunda.bpm.jboss.camunda-jboss-subsystem">
  ...
  <dependencies>
    ...
    <!-- ElasticSearch plugin -->
    <module name="org.camunda.bpm.elasticsearch" />
  </dependencies>
</module>
```

* Edit the JBoss `standalone.xml` file and activate the Elasticsearch plugin:

```xml
<subsystem xmlns="urn:org.camunda.bpm.jboss:1.1">
  <process-engines>
    <process-engine name="default" default="true">
      ...
      <plugins>
        <plugin>
          <class>org.camunda.bpm.elasticsearch.ElasticSearchHistoryPlugin</class>
        </plugin>
      </plugins>
    </process-engine>
  </process-engines>
<subsystem>
```

##### Installation in Tomcat Distribution

In order to install the Elasticsearch process engine plugin in the Camunda Tomcat distribution, the
following steps are necessary:

* Download a pre-packaged distribution from [camunda.org](http://camunda.org/download/).
* Unpack the camunda distribution into a folder of your choice.
* Clone the [camunda-bpm-elasticsearch][repository] repository using Git.
* Build the repository by typing `mvn clean install`
* copy the libraries `elasticsearch-lib-module/target/lib/` to the apache tomcat `lib/` folder (the
  same folder in which the `camunda-engine.jar` file is located.).
* Inside the apache tomcat configuration folder, edit the file named `bpm-platform.xml`:

```xml
<process-engine name="default">
  ...
  <plugins>
    <plugin>
      <class>org.camunda.bpm.elasticsearch.ElasticSearchHistoryPlugin</class>
    </plugin>
  </plugins>
</process-engine>
```

### Installing the Cockpit Plugin

> TODO: this section needs to be improved

In order to install the cockpit plugin you need to proceed as follows:

* Build the cockpit plugin by cloning the repository and building it with maven
* Copy the plugin jar file to the WEB-INF/lib folder of the camunda Webapplication

[userguide:history_event_stream]: http://docs.camunda.org/latest/guides/user-guide/#process-engine-history-and-audit-event-log
[quick-search-screenshot]: img/quick-search.png
[process-engine-plugin]: http://docs.camunda.org/latest/guides/user-guide/#process-engine-process-engine-plugins
[repository]: https://github.com/camunda/camunda-bpm-elasticsearch
