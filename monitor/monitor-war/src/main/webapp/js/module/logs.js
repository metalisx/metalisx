function LogsController($scope, $compile, $http, $location, $routeParams, 
			crudService, utilsService, applicationContext) {

	var logUrl = '/log/';
	var logsUrl = 'logs.html?showList=true&immediate=true';
	var logJsonEndpoint = applicationContext.contextPath + '/rest/logs';
	var dataTableSelector = '#dataTable';

	$scope.dataTableEnabled = false;
	$scope.dataTableSettings = null;
	
	$scope.dataTableFilterInitial = null;
	$scope.dataTableFilter = {};

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
				"sAjaxSource": crudService.getPageEndpoint(logJsonEndpoint),
				"aaSorting": [[0, 'desc']],
		        "aoColumns": [
					{ "sName": "logDate",
						"mDataProp": "logDate", 
						"fnRender": function ( oObj, sVal ) {
							return $.metalisxUtils.isoDateAsStringtoDisplayDate(sVal);
						}},
					{ "sName": "organization",
						"mDataProp": "organization",
						"fnRender": function ( oObj, sVal ) {
							return '<a href="' + logsUrl + '&organization=' + sVal + '">' + 
										sVal + '</a>';
						}},
					{ "sName": "username",
						"mDataProp": "username",
						"fnRender": function ( oObj, sVal ) {
							return '<a href="' + logsUrl + '&username=' + sVal + '">' + 
										sVal + '</a>';
						}},
					{ "sName": "message",
						"mDataProp": "message",
						"fnRender": function ( oObj, sVal ) {
							return '<a href="' + logsUrl + '&message=' + sVal + '">' + 
										sVal + '</a>';
						}},
					{ "sName": "duration",
						"mDataProp": "duration" }
				]
			}
		};
	};

	// Init
	
	function init() {
		initDataTable();
		crudService.getFilter(logJsonEndpoint, {onsuccess: function(result) {
			$scope.dataTableFilterInitial = result.item;
			initDataTableFilter();
			$scope.dataTableEnabled = true;
			$scope.$apply();
		}});
	}
	
	$scope.$on('$routeUpdate', function(next, current) {        
		initDataTableFilter();
		$scope.refreshDataTable();
	});
	
	// Actions
	
	$scope.search = function($event) {
		$event.stopPropagation();
		$event.preventDefault();
		$location.search($scope.dataTableFilter);
		$scope.refreshDataTable();
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

	// Helpers
	
	$scope.refreshDataTable = function() {
		$(dataTableSelector).dataTable().fnDraw();
	}

	// Filter

	function initDataTableFilter() {
		$.extend(true, $scope.dataTableFilter, $scope.dataTableFilterInitial);
		if (!utilsService.isUrlParamEmpty($routeParams.url)) {
			$scope.dataTableFilter.url = $routeParams.url;
		}
		if (!utilsService.isUrlParamEmpty($routeParams.range)) {
			$scope.dataTableFilter.range = $routeParams.range;
		}
		if (!utilsService.isUrlParamEmpty($routeParams.startDate)) {
			$scope.dataTableFilter.startDate = $routeParams.startDate;
		}
		if (!utilsService.isUrlParamEmpty($routeParams.endDate)) {
			$scope.dataTableFilter.endDate = $routeParams.endDate;
		}
		if (!utilsService.isUrlParamEmpty($routeParams.sessionId)) {
			$scope.dataTableFilter.sessionId = $routeParams.sessionId;
		}
		if (!utilsService.isUrlParamEmpty($routeParams.requestId)) {
			$scope.dataTableFilter.requestId = $routeParams.requestId;
		}
		if (!utilsService.isUrlParamEmpty($routeParams.organization)) {
			$scope.dataTableFilter.organization = $routeParams.organization;
		}
		if (!utilsService.isUrlParamEmpty($routeParams.username)) {
			$scope.dataTableFilter.username = $routeParams.username;
		}
		if (!utilsService.isUrlParamEmpty($routeParams.showList)) {
			$scope.dataTableFilter.showList = $routeParams.showList;
		}
		if (!utilsService.isUrlParamEmpty($routeParams.showChart)) {
			$scope.dataTableFilter.showChart = $routeParams.showChart;
		}
		if (!utilsService.isUrlParamEmpty($routeParams.showOverviewChart)) {
			$scope.dataTableFilter.showOverviewChart = $routeParams.showOverviewChart;
		}
		if (!utilsService.isUrlParamEmpty($routeParams.realtime)) {
			$scope.dataTableFilter.realtime = $routeParams.realtime;
		}
	}
	
	// Realtime update

	$scope.timer = null;
	
	function realtimeUpdate() {
		renderAll(function() {
			if ($scope.dataTableFilter.realtime) {
				$scope.timer = setTimeout(realtimeUpdate, $scope.dataTableFilter.realtimeInterval);
			}
		});
	}
	
	$scope.removeRealtimeUpdateTimer = function() {
		if ($scope.timer != null) {
			clearTimeout($scope.timer);
			$scope.timer = null;
		}
	};
	
	$scope.toggleRealtimeChartUpdate = function() {
		if ($scope.dataTableFilter.realtime) {
			if ($scope.timer == null) {
				realtimeUpdate();
			}
		} else {
			$scope.removeRealtimeUpdateTimer();
		}
	};
	
	// Displaying
	
	var logsSelector = '#logs';
	var lineChartContainerSelector = '#lineChartContainer';
	var lineChartOverviewSelector = '#lineChartOverview';

	function renderCharts(onsuccess) {
		if ($scope.dataTableFilter.showChart) {
			getChart($scope.dataTableFilter, function(chartData) {
				if (chartData) {
					$(lineChartContainerSelector).show(); // To show canvas labels the chart has to be visible first.
					renderChart(chartData, function(plot, chartData) {
						if ($scope.dataTableFilter.showOverviewChart) {
							getOverview($scope.dataTableFilter, chartData, function(overviewData) {
								if (overviewData) {
									for (prop in $scope.overviewSettings) {
										delete $scope.overviewSettings[prop]; 
									}
									if (overviewData && overviewData.settings) {
										$.extend(true, $scope.overviewSettings, overviewData.settings);
									}
									$(lineChartOverviewSelector).show();
									renderOverview(plot, overviewData, function() {
										if (onsuccess) {
											onsuccess();
										}
									});
								}
							});
						} else {
							$(lineChartOverviewSelector).hide();
							if (onsuccess) {
								onsuccess();
							}
						}
					});
				}
			});
		} else {
			$(lineChartContainerSelector).hide();
			if (onsuccess) {
				onsuccess();
			}
		}
	};
	
	function renderDataTable(onsuccess) {
		if ($scope.dataTableFilter.showList) {
			$(logsSelector).show();
			$scope.refreshDataTable();
			if (onsuccess) {
				onsuccess();
			}
		} else {
			$(logsSelector).hide();
			if (onsuccess) {
				onsuccess();
			}
		}
	};

	function renderAll(onsuccess) {
		if ($scope.dataTableFilter.showList && $scope.dataTableFilter.showChart) { // render chart and list after each other is required for proper use of timer
			renderDataTable(function() {
				renderCharts(onsuccess);
			});
		} else if ($scope.dataTableFilter.showList) {
			renderDataTable(onsuccess);
			renderCharts();
		} else if ($scope.dataTableFilter.showChart) {
			renderDataTable();
			renderCharts(onsuccess);
		} else {
			renderDataTable();
			renderCharts();
			if (onsuccess) {
				onsuccess();
			}
		}
	};
	
	////////////
	// Charts //
	////////////

	// Charts actions
	
	$scope.toggleShowCharts = function() {
		renderAll();
	};
	
	$scope.toggleShowList = function() {
		renderAll();
	};
	
	// Charts service
	
	var urlChartEndpoint = '../rest/logs/chart';
	var urlOverviewEndpoint = '../rest/logs/overview';
	var urlOverviewSettingsEndpoint = '../rest/logs/overviewSettings';
	
	function getChart(filter, onsuccess) {
		crudService.post(urlChartEndpoint, filter, {onsuccess: function(result) {
			if (onsuccess) {
				onsuccess(result);
			}
		}});
	};
	
	function getOverview(filter, chartData, onsuccess) {
		// The filter needs to be cloned because it does not always contain 
		// a start and and date. In this case the boundaries of the 
		// chart are used as start and end date for the overview filter.
		var overviewFilter = jQuery.extend({}, filter);
		overviewFilter.startDate = chartData.settings.min;
		overviewFilter.endDate = chartData.settings.max;
		crudService.post(urlOverviewEndpoint, overviewFilter, {onsuccess: function(result) {
			if (onsuccess) {
				onsuccess(result);
			}
		}});
	};

	function getOverviewSettings(overviewSettings, onsuccess) {
		crudService.post(urlOverviewSettingsEndpoint, overviewSettings, {onsuccess: function(result) {
			if (onsuccess) {
				onsuccess(result);
			}
		}});
	};
	
	// Charts renderer
	
	$scope.overviewSettings = {};

	function renderChart(chartData, onsuccess) {
		
		var lineChartSelector = '#lineChart';
		
		var $this = this;
		
		$(lineChartSelector).metalisxTimeLineChart(chartData, {
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
					$scope.dataTableFilter.startDate = $.metalisxUtils.dateToIsoDateAsString(new Date(ranges.xaxis.from));
					$scope.dataTableFilter.endDate = $.metalisxUtils.dateToIsoDateAsString(new Date(ranges.xaxis.to));
					$scope.dataTableFilter.range = 'custom';
					$scope.dataTableFilter.realtime = false;
					$scope.$digest();
					getChart($scope.dataTableFilter, function(chartData) {
						$this.renderChart(chartData);
						if ($scope.dataTableFilter.showList) {
							$this.renderDataTable($scope);
						}
						$scope.removeRealtimeUpdateTimer();
					});
	        		plot.clearSelection();
				});
				if (onsuccess) {
					onsuccess(plot, chartData);
				}
			}
		});
	};
	
	function renderOverview(plot, overviewData, onsuccess) {

		var lineChartOverviewSelector = '#lineChartOverview';

		$(lineChartOverviewSelector).metalisxTimeLineChart(overviewData, {
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
					$scope.overviewSettings.selectionStartDate = $.metalisxUtils.dateToIsoDateAsString(new Date(ranges.xaxis.from));
					$scope.overviewSettings.selectionEndDate = $.metalisxUtils.dateToIsoDateAsString(new Date(ranges.xaxis.to));
					getOverviewSettings($scope.overviewSettings, function(overviewSettingsData) {
						if (overviewSettingsData) {
							$.extend(true, $scope.overviewSettings, overviewSettingsData);
							$scope.$digest();
							ranges.xaxis.from = $.metalisxUtils.isoDateAsStringtoDate($scope.overviewSettings.selectionStartDate).getTime(); 
							ranges.xaxis.to = $.metalisxUtils.isoDateAsStringtoDate($scope.overviewSettings.selectionEndDate).getTime();
							plot.setSelection(ranges);
						}
					});
					overview.clearSelection();
				});
				if (onsuccess) {
					onsuccess(overview, overviewData);
				}
			}
		});		
	};
	
	init();
}
