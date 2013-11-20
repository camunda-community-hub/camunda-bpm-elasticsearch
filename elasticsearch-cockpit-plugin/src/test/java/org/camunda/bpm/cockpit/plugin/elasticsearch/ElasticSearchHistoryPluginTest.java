package org.camunda.bpm.cockpit.plugin.elasticsearch;

import org.camunda.bpm.cockpit.Cockpit;
import org.camunda.bpm.cockpit.plugin.spi.CockpitPlugin;
import org.camunda.bpm.cockpit.plugin.test.AbstractCockpitPluginTest;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class ElasticSearchHistoryPluginTest extends AbstractCockpitPluginTest {

  @Test
  public void testPluginDiscovery() {
    CockpitPlugin elasticsearchHistoryPlugin = Cockpit.getRuntimeDelegate()
        .getPluginRegistry()
        .getPlugin(ElasticSearchHistoryPlugin.ID);

    Assert.assertNotNull(elasticsearchHistoryPlugin);
  }

  @Ignore
  @Test
  public void empty() {

  }

}
