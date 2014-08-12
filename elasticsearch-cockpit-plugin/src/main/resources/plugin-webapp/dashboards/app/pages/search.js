ngDefine('dashboards.pages.search', [
  'angular',
  'require',
], function(module, angular, require) {

  var Controller = ['$scope', '$http', 'Uri', '$compile',
            function($scope, $http, Uri, $compile) {

    $scope.searchString = "";
    $scope.includeVariables = true;
    $scope.includeTasks = true;
    $scope.includeActivities = false;

    $scope.searchResults = [];

    $scope.doSearch = function() {

      $http({
        method: 'GET',
        url: Uri.appUri("plugin://dashboards/default/search"),
        params: {
          query: $scope.searchString,
          includeVariables: $scope.includeVariables,
          includeTasks: $scope.includeTasks,
          includeActivities: $scope.includeActivities
        }
      }).success(function(data) {
        $scope.searchResults = data;
      });

    };

    $scope.clear = function() {
      $scope.searchResults = [];
      $scope.searchString = "";
    };

  }];

  var ViewConfig = [ 'ViewsProvider', function(ViewsProvider) {
    ViewsProvider.registerDefaultView('cockpit.dashboard', {
      id: 'search',
      priority: 20,
      label: 'Seach',
      controller: Controller,
      url: require.toUrl('./search.html')
    });
  }];

  module
    .config(ViewConfig);
});
