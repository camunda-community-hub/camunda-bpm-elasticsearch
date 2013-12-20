ngDefine('dashboards.pages.processDefinition', [
  'angular',
  'require'
], function(module, angular, require) {

  var Controller = ['$scope', 'processDefinition', 'Views',
            function($scope, processDefinition, Views) {

    $scope.processDefinition = processDefinition;

    $scope.exportedVars = { read: [ 'processDefinition' ] };
    $scope.dashboardProviders = Views.getProviders({ component: 'dashboard.processDefinition.view' });
  }];

  var RouteConfig = [ '$routeProvider', 'AuthenticationServiceProvider', function($routeProvider, AuthenticationServiceProvider) {
    $routeProvider.when('/process-definition/:processDefinitionId/dashboard', {
      templateUrl: require.toUrl('./process-definition.html'),
      controller: Controller,
      resolve: {
        authenticatedUser: AuthenticationServiceProvider.requireAuthenticatedUser,
        processDefinition: [ 'ResourceResolver', 'ProcessDefinitionResource',
          function(ResourceResolver, ProcessDefinitionResource) {
            return ResourceResolver.getByRouteParam('processDefinitionId', {
              name: 'process definition',
              resolve: function(id) {
                return ProcessDefinitionResource.get({ id : id });
              }
            });
          }]
      },
      reloadOnSearch: false
    });
  }];

  module.config(RouteConfig);
});
