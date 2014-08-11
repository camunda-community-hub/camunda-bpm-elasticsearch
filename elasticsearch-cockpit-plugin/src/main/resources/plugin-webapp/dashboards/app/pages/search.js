ngDefine('dashboards.pages.search', [
  'angular',
  'require'
], function(module, angular, require) {

  var Controller = ['$scope', '$http', 'Uri',
            function($scope, $http, Uri) {

    $scope.searchString = "";

    $scope.doSearch = function() {

      $http({
        method: 'GET',
        url: Uri.appUri("plugin://dashboards/default/search"),
        params: {query: $scope.searchString}
      }).success(function(data) {
        $scope.searchResult = data;
      });

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
