#!/bin/sh
distro/elasticsearch-1.4.4/bin/elasticsearch --node.name=camunda_bpm_remote_es --cluster.name=camunda_bpm_es_cluster -Des.script.disable_dynamic=false
