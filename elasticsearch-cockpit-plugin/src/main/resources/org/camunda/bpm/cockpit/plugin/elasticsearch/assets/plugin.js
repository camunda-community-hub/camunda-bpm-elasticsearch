ngDefine('cockpit.plugin.elasticsearchHistory', [
  'module:camunda-bpm-elasticsearch.views:./views/main',
//  'module:cockpit.plugin.base.resources:./resources/main'
], function(module) {

  var SearchBarController = ['$http', function($http) {

  }];

  var PluginConfiguration = [ 'ViewsProvider', function PluginConfiguration(ViewsProvider) {
    ViewsProvider.registerDefaultView('cockpit.dashboard', {
      id: 'search-bar', //'process-definition-list',
      label: 'Search Processes',
      url: 'plugin://base/static/app/views/dashboard/process-definition-list.html',
      controller: SearchBarController,
      priority: 20
    });
  }];

  module.config(PluginConfiguration);

  return module;
});
