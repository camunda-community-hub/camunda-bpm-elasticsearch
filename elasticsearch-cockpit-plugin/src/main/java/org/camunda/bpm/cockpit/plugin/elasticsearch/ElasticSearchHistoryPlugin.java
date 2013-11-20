package org.camunda.bpm.cockpit.plugin.elasticsearch;

import org.camunda.bpm.cockpit.plugin.elasticsearch.resources.ElasticSearchHistoryPluginRootResource;
import org.camunda.bpm.cockpit.plugin.spi.impl.AbstractCockpitPlugin;

import java.util.HashSet;
import java.util.Set;

public class ElasticSearchHistoryPlugin extends AbstractCockpitPlugin {

  public static final String ID = "elasticsearchHistory";

  @Override
  public String getId() {
    return ID;
  }

  @Override
  public Set<Class<?>> getResourceClasses() {
    Set<Class<?>> classes = new HashSet<Class<?>>();

    classes.add(ElasticSearchHistoryPluginRootResource.class);

    return classes;
  }

  @Override
  public String getAssetDirectory() {
    return "webapp://plugin/sample";
  }
}
