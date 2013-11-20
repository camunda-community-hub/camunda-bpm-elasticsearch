camunda BPM history events to Elasticsearch mapping
===================================================

index -> camunda-bpm

type -> HistoricVariableUpdateEventEntity -> variable
type -> HistoricFormPropertyEventEntity -> formproperty
type -> HistoricProcessInstanceEventEntity -> processinstance
type -> HistoricTaskInstanceEventEntity -> task
type -> HistoricActivityInstanceEventEntity -> activityinstance

type -> HistoricDetailEventEntity ???


ES mapping
==========

  HistoryEvent

    protected String id;                        -> not_analysed
    protected String processInstanceId;         -> not_analysed
    protected String executionId;               -> not_analysed
    protected String processDefinitionId;       -> not_analysed


  HistoricDetailEvent

    protected String activityInstanceId;        -> not_analysed
    protected String taskId;                    -> not_analysed
    protected Date timestamp;


  HistoricVariableUpdateEvent

    protected String variableName;
    protected String variableInstanceId;
    protected int revision;                     -> not mapped
    protected String variableTypeName;
    protected Long longValue;
    protected Double doubleValue;
    protected String textValue;                 -> not_analysed, analysed
    protected String textValue2;                -> not_analysed, analysed
    protected byte[] byteValue;
    protected String byteArrayId;

  HistoricFormPropertyEvent

    protected String propertyId;
    protected String propertyValue;

  HistoricActivityInstanceEvent

    protected String activityId;                -> not_analysed
    protected String activityName;              -> not_analysed, analysed
    protected String activityType;
    protected String activityInstanceId;        -> not_analysed
    protected String parentActivityInstanceId;  -> not_analysed
    protected String calledProcessInstanceId;   -> not_analysed
    protected String taskId;                    -> not_analysed
    protected String taskAssignee;              -> not_analysed, analysed
    protected Long durationInMillis;
    protected Date startTime;
    protected Date endTime;

  HistoricProcessInstanceEvent

    protected String businessKey;               -> not_analysed, analysed
    protected String startUserId;
    protected String superProcessInstanceId;    -> not_analysed
    protected String deleteReason;
    protected Long durationInMillis;
    protected Date startTime;
    protected Date endTime;
    protected String endActivityId;             -> not_analysed
    protected String startActivityId;           -> not_analysed

  HistoricTaskInstanceEvent

    protected String taskId;                    -> not_analysed
    protected String assignee;                  -> not_analysed, analysed
    protected String owner;                     -> not_analysed, analysed
    protected String name;                      -> not_analysed, analysed
    protected String description;
    protected Date dueDate;
    protected int priority;
    protected String parentTaskId;              -> not_analysed
    protected String deleteReason;
    protected String taskDefinitionKey;
    protected Long durationInMillis;
    protected Date startTime;
    protected Date endTime;

id -> id of the event?

${ES_HOME_URL}/${index}/${type}/${id}


TODO
====

  * Initialize ESHistoryEventDispatcher by configuration file
  * Build mechanism to exclude fields from the history events which should not be serialized by Jackson.
  * Look into transaction participation with the engine -> TransactionListener set into own Session (openSession) / SessionFactory
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
    * json filtering
    * queries (possible queries, facets)

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

