function LogsController($scope, $compile, $http, $location, $routeParams, $timeout,
			crudService, utilsService, applicationContext) {

	var logUrl = '/logs/log/';
	var logsUrl = 'index.html#/logs?showList=true&immediate=true';
	var logJsonEndpoint = applicationContext.contextPath + '/rest/logs';

	$scope.dataTableEnabled = false;
	$scope.dataTableSettings = null;
	
	$scope.dataTableFilterInitial = null;
	$scope.dataTableFilter = {};

	$scope.flotEnabled = false;
	$scope.flotFilter = {};
	$scope.flotSettings = null;
	$scope.flotCallback = null;
	$scope.flotOverviewEnabled = false;
	$scope.flotOverviewFilter = {};
	$scope.flotOverviewSettings = null;

	// DataTable
	function initDataTable() {
		
		$scope.dataTableSettings = {
			onRowClick: function(nRow, aData, iDataIndex) {
				$scope.$apply(function() {
					$location.path(logUrl + aData.id);
				});
			},
			onsuccessRow: function(nRow, aData, iDataIndex) {
				var html = $(nRow).html();
				$(nRow).html(
					$compile(html)($scope)
				);
			},
			"dataTableSettings": {
				"ajax": crudService.getPageEndpoint(logJsonEndpoint),
				"sorting": [[0, 'desc']],
		        "columns": [
					{ "name": "logDate",
						"data": "logDate", 
						"render": function (data, type, full, meta) {
							var logDate = data == null ? '' : $.metalisxUtils.toDisplayDate(data);
							return logDate;
						}},
					{ "name": "organization",
						"data": "organization",
						"render": function (data, type, full, meta) {
							var organization = data == null ? '' : data;
							return '<a href="' + logsUrl + '&organization=' + organization + '">' + 
										organization + '</a>';
						}},
					{ "name": "username",
						"data": "username",
						"render": function (data, type, full, meta) {
							var username = data == null ? '' : data;
							return '<a href="' + logsUrl + '&username=' + username + '">' + 
										username + '</a>';
						}},
					{ "name": "message",
						"data": "message",
						"render": function (data, type, full, meta) {
							var message = data == null ? '' : data;
							return '<a href="' + logsUrl + '&message=' + message + '" ngc-stop-propagation>' + 
										message + '</a>';
						}},
					{ "name": "duration",
						"data": "duration" }
				]
			}
		};
	};

	// Init
	
	function init() {
		initDataTable();
		initCharts();
		crudService.getFilter(logJsonEndpoint, {onsuccess: function(result) {
			// We watch the dataTableFilter for changes, with the deep dirty check enabled.
			// If there is a change in the dataTableFilter the values are copied
			// to the flotFilter and the flotOverviewFilter.
			// Then we set the startDate and endDate of the flotOverviewFilter
			// to null because we want a full overview.
			$scope.$watch('dataTableFilter', function(newValue, oldValue) {
				$.extend(true, $scope.flotFilter, $scope.dataTableFilter);
				$.extend(true, $scope.flotOverviewFilter, $scope.dataTableFilter);
			}, true);
			$scope.dataTableFilterInitial = result.item;
			$.extend(true, $scope.dataTableFilter, $scope.dataTableFilterInitial);
			initDataTableFilter();
			$scope.dataTableEnabled = true;
			$scope.flotEnabled = true;
			$scope.$apply();
		}});
	}
	
	$scope.$on('$routeUpdate', function(next, current) {
		initDataTableFilter();
		$scope.refreshDataTable();
		$scope.refreshChart();
	});
	
	// Actions
	
	$scope.search = function($event) {
		$event.stopPropagation();
		$event.preventDefault();
		$location.search($scope.dataTableFilter);
	}
	
	$scope.changeRange = function() {
		if ($scope.dataTableFilter.range != 'custom') {
			$scope.dataTableFilter.startDate = null;
			$scope.dataTableFilter.endDate = null;
		} else {
			$scope.dataTableFilter.realtime = false;
			$scope.removeRealtimeUpdateTimer();
		}
	};

	$scope.updateLocationSearch = function($event) {
		$location.search($scope.dataTableFilter);
	};
	
	// Helpers
	
	$scope.refresh = function(onsuccess) {
		$scope.refreshDataTable();
		$scope.refreshChart();
	}
	
	$scope.refreshDataTable = function() {
		$timeout(function() {
			if ($scope.dataTableFilter.showList === true) {
				// When it is true then set it to false, so the watch event in the directive is triggered.
				if ($scope.dataTableEnabled === true) {
					$scope.dataTableEnabled = false;
					$scope.$apply();
				}
				$scope.dataTableEnabled = true;
				$scope.$apply();
			}
		});
	}
	
	$scope.refreshChart = function() {
		$timeout(function() {
			if ($scope.dataTableFilter.showChart === true) {
				// When it is true then set it to false, so the watch event in the directive is triggered.
				if ($scope.flotEnabled === true) {
					$scope.flotEnabled = false;
					$scope.$apply();
				}
				$scope.flotEnabled = true;
				$scope.$apply();
			}
		});
	}

	$scope.refreshOverviewChart = function() {
		$timeout(function() {
			if ($scope.dataTableFilter.showOverviewChart === true) {
				// When it is true then set it to false, so the watch event in the directive is triggered.
				if ($scope.flotOverviewEnabled === true) {
					$scope.flotOverviewEnabled = false;
					$scope.$apply();
				}
				$scope.flotOverviewEnabled = true;
				$scope.$apply();
			}
		});
	}

	// Filter

	function initDataTableFilter() {
		var $params = $location.search();
		if ($.isEmptyObject($params)) {
			$.extend(true, $scope.dataTableFilter, $scope.dataTableFilterInitial);
		} else {
			if (!utilsService.isUrlParamEmpty($params.message)) {
				$scope.dataTableFilter.message = $params.message;
			}
			if (!utilsService.isUrlParamEmpty($params.range)) {
				$scope.dataTableFilter.range = $params.range;
			}
			if (!utilsService.isUrlParamEmpty($params.startDate)) {
				$scope.dataTableFilter.startDate = $params.startDate;
			}
			if (!utilsService.isUrlParamEmpty($params.endDate)) {
				$scope.dataTableFilter.endDate = $params.endDate;
			}
			if (!utilsService.isUrlParamEmpty($params.sessionId)) {
				$scope.dataTableFilter.sessionId = $params.sessionId;
			}
			if (!utilsService.isUrlParamEmpty($params.requestId)) {
				$scope.dataTableFilter.requestId = $params.requestId;
			}
			if (!utilsService.isUrlParamEmpty($params.organization)) {
				$scope.dataTableFilter.organization = $params.organization;
			}
			if (!utilsService.isUrlParamEmpty($params.username)) {
				$scope.dataTableFilter.username = $params.username;
			}
			if (!utilsService.isUrlParamEmpty($params.showList)) {
				$scope.dataTableFilter.showList = $params.showList === false || $params.showList === 'false' ? false : true;
			}
			if (!utilsService.isUrlParamEmpty($params.showChart)) {
				$scope.dataTableFilter.showChart = $params.showChart === false || $params.showChart === 'false' ? false : true;
			}
			if (!utilsService.isUrlParamEmpty($params.showOverviewChart)) {
				$scope.dataTableFilter.showOverviewChart = $params.showOverviewChart === false || $params.showOverviewChart === 'false' ? false : true;
			}
			if (!utilsService.isUrlParamEmpty($params.realtime)) {
				$scope.dataTableFilter.realtime = $params.realtime;
			}
			if (!utilsService.isUrlParamEmpty($params.realtimeInterval)) {
				$scope.dataTableFilter.realtimeInterval = $params.realtimeInterval;
			}
		}
	}
	
	// Realtime update

	$scope.timer = null;
	
	function realtimeUpdate() {
		$scope.refresh();
		if ($scope.dataTableFilter.realtime) {
			$scope.timer = setTimeout(realtimeUpdate, $scope.dataTableFilter.realtimeInterval);
		}
	}
	
	$scope.removeRealtimeUpdateTimer = function() {
		if ($scope.timer != null) {
			clearTimeout($scope.timer);
			$scope.timer = null;
		}
	};
	
	$scope.toggleRealtime = function() {
		$location.search($scope.dataTableFilter);
		if ($scope.dataTableFilter.realtime) {
			if ($scope.timer == null) {
				realtimeUpdate();
			}
		} else {
			$scope.removeRealtimeUpdateTimer();
		}
	};
	
	// Charts
	
	function initCharts() {
		
		$scope.flotCallback = function(plot, result) {
			if (result && result.settings) {
				$scope.flotOverviewFilter.startDate = result.settings.min;
				$scope.flotOverviewFilter.endDate = result.settings.max;
				$scope.refreshOverviewChart();
			}
		}
		
		$scope.flotSettings = {
				getX: function (index, item) {
					return item.logDate;
				},
				getY: function (index, item) { 
					return item.duration; 
				},
				getXaxisMin: function(data) {
					return data.settings.min;
				}, 
				getXaxisMax: function(data) {
					return data.settings.max;
				},
				xaxisTicks: function(items) {
					return 5;
				},
				flotSettings: {
					xaxis: {
						min: $scope.dataTableFilter.startDate, 
						max: $scope.dataTableFilter.endDate 
					},
					selection: { 
						mode: "x" 
					}
				},
				onsuccess: function(plot, chartData) {
					plot.getPlaceholder().off("plotselected"); // clean up an eventual previous set event
					plot.getPlaceholder().on("plotselected", function (event, ranges) {
						$scope.dataTableFilter.startDate = $.metalisxUtils.toIsoDate(new Date(ranges.xaxis.from));
						$scope.dataTableFilter.endDate = $.metalisxUtils.toIsoDate(new Date(ranges.xaxis.to));
						$scope.dataTableFilter.range = 'custom';
						$scope.dataTableFilter.realtime = false;
						$location.search($scope.dataTableFilter);
						// Refresh chart and dataTable
						$scope.refresh();
						// Remove timer
						$scope.removeRealtimeUpdateTimer();
		        		plot.clearSelection();
					});
				}
			};
		
		$scope.flotOverviewSettings = {
				getX: function (index, item) {
					return item.date;
				},
				getY: function (index, item) { 
					return item.duration; 
				},
				getXaxisMin: function(data) {
					return data.settings.min;
				}, 
				getXaxisMax: function(data) {
					return data.settings.max;
				},
				flotSettings: {
			        series: {
			            points: { show: false }
			        },
					xaxis: {
						ticks: 2
				    },
			        yaxis: { ticks: []},
					selection: {
						mode: "x" 
					},
					grid: {
						hoverable: false
					}
				},
				showSaveButton: false,
		        onsuccess: function(overview, overviewData) {
					overview.getPlaceholder().off("plotselected"); // clean up an eventual previous set event
					overview.getPlaceholder().on("plotselected", function (event, ranges) {
						$scope.dataTableFilter.startDate = $.metalisxUtils.toIsoDate(new Date(ranges.xaxis.from));
						$scope.dataTableFilter.endDate = $.metalisxUtils.toIsoDate(new Date(ranges.xaxis.to));
						$scope.dataTableFilter.range = 'custom';
						$scope.dataTableFilter.realtime = false;
						$location.search($scope.dataTableFilter);
						// Refresh chart and dataTable
						$scope.refresh();
						// Remove timer
						$scope.removeRealtimeUpdateTimer();
						overview.clearSelection();
					});
				}
			};
	}
	
	init();
}


application.directive('ngcFlot', function(crudService) {
	
    return {
		restrict: 'A',
		scope: {ngcFlot: '@',
				ngcFlotEnabled:'=',
    			ngcFlotSettings:'=',
    			ngcFlotFilter: '=',
    			ngcFlotCallback: '&'},
        link:function (scope, element, attrs) {
    		
        	var url = null;
			var filter = null;
			var settings = null;
			var callback = null;
        	var plot = null;
        	
			if (attrs['ngcFlot'] == null || attrs['ngcFlot'] == '') {
				alert('The ngc-flot attribute is missing the value of the REST endpoint.');
			} else {
				url = scope.ngcFlot;
			}
			if (attrs['ngcFlotFilter'] != null && attrs['ngcFlotFilter'] != '') {
				filter = scope.ngcFlotFilter;
			}
			if (attrs['ngcFlotSettings'] != null && attrs['ngcFlotSettings'] != '') {
				settings = scope.ngcFlotSettings;
			}
			if (attrs['ngcFlotCallback'] && attrs['ngcFlotCallback'] != '') {
				callback = scope.ngcFlotCallback;
			}
			
			function initFlot() {
				crudService.post(url, filter, {onsuccess: function(result) {
					if (result && result.items && result.settings) {
		    			plot = $(element).metalisxTimeLineChart(result, settings);
		    			if (callback) {
		    				callback({
		    					plot: plot,
		    					result: result
		    				});
		    			}
		    			// Destroy the flot when the element is removed from the DOM.
		    			// This is required when the ng-view is used.
		    			element.bind("$destroy", function() {
		    				if (plot != null) {
		    					plot.shutdown();
		    				}
		    	        });
					}
				}});
    		}

    		if ($.plot === undefined) {
    			alert('Please include the Flot javascript files or remove the AngularJS directive from the element.');
    		} else {
    			var enabled = true;
    			if (attrs['ngcFlotEnabled'] && attrs['ngcFlotEnabled'] != '' && scope.ngcFlotEnabled === false) {
    				enabled = false;
    				// We watch if it is changed from disabled to enabled, not the otherway arround.
    				// If it is changed to true the element is promoted to a flot.
    				scope.$watch("ngcFlotEnabled", function() {
    					if (scope.ngcFlotEnabled === true) {
    						initFlot();
    					}
    			   });
    			}
    			// If it is not enabeld we do nothing.
    			if (enabled) {
    				initFlot();
    			}
    		}
        }
    };
    
});
