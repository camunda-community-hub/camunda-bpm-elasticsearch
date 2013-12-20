ngDefine('elasticsearch.views.processDefinitionDashboard', [
  'require',
  '../lib/elastic',
  'module:elasticjs.service:../lib/elastic-angular-client'
], function(module, require) {

  module.factory('elasticBackend', [ 'ejsResource', function(ejsResource) {
    // TODO: configure ejs endpoint
    return ejsResource();
  }]);

  var Controller = [ '$scope', 'elasticBackend', function($scope, elasticBackend) {

    // TODO: continue from here. Perform great queries, do other awesome things.
    $scope.hi = "HI";
  }];

  var Configuration = [ 'ViewsProvider', function(ViewsProvider) {

    ViewsProvider.registerDefaultView('dashboard.processDefinition.view', {
      id: 'elastic-search-procdef-dashboard',
      label: 'Big Data Dashboards',
      url: require.toUrl('./process-definition-dashboard.html'),
      controller: Controller,
      priority: 7
    });
  }];

  module.config(Configuration);
});