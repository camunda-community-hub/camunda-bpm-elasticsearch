ngDefine('dashboards.pages.activityMonitor', [
  'angular',
  'require',
  '../lib/freewall',
  '../lib/d3.min',
  '../lib/nv.d3.min',
  'module:nvd3:../lib/angular-nvd3'
], function(module, angular, require) {

  var Controller = ['$scope', '$http', 'Uri', '$compile', '$timeout',
            function($scope, $http, Uri, $compile, $timeout) {

    $('head').append('<link rel="stylesheet" href="'+require.toUrl('../lib/nv.d3.min.css')+'" type="text/css" />');

    $scope.interval = '1s';
    $scope.timeframe = undefined;
    $scope.options = {
      chart: {
        type: 'stackedAreaChart',
        height: 400,
        margin : {
          top: 20,
          right: 20,
          bottom: 60,
          left: 40
        },
        x: function(bucket){return bucket.key;},
        y: function(bucket){return bucket.docCount;},
        useVoronoi: false,
        clipEdge: true,
        transitionDuration: 1,
        useInteractiveGuideline: true,
        xAxis: {
          showMaxMin: true,
          tickFormat: function(d) {
            return d3.time.format('%x')(new Date(d));
          }
        },
        yAxis: {
          tickFormat: function(d){
            return d3.format(',.2f')(d);
          }
        }
      }
    };


    $scope.getHistogramData = function() {


      $http({
        method: 'GET',
        url: Uri.appUri("plugin://dashboards/default/histogram/processinstance"),
        params: {
          interval: $scope.interval,
          timeframe: $scope.timeframe
        }
      }).success(function(data) {
        histogramData = [];


        var values = [];


        $scope.data = [];
        var ended = { "key": "Ended Process Instances", "values": [] };
        $scope.data.push(ended);

        angular.forEach(data.dateHistogramBuckets.ended, function(bucket) {
          $scope.data[0].values.push(bucket);
        }, values);

        $timeout($scope.getHistogramData, 2000);
      });

    };

    $scope.getHistogramData();
  }];

  var ViewConfig = [ 'ViewsProvider', function(ViewsProvider) {
    ViewsProvider.registerDefaultView('cockpit.dashboard', {
      id: 'activity-monitor',
      priority: 19,
      label: 'Activity Monitor',
      controller: Controller,
      url: require.toUrl('./activity-monitor.html')
    });
  }];

  module
    .config(ViewConfig);
});
