application.service('logsService', LogsService);

application.service('logsRenderer', LogsRenderer);

function LogsService(crudService) {
	
	var filterEndpoint = '../rest/logs/filter';
	var urlChartEndpoint = '../rest/logs/chart';
	var urlOverviewEndpoint = '../rest/logs/overview';
	var urlOverviewSettingsEndpoint = '../rest/logs/overviewSettings';

	this.getFilter = function(onsuccess) {
		crudService.post(filterEndpoint, $.urlValuesToObject(), {onsuccess: onsuccess});
	};

	this.getChart = function(filter, onsuccess) {
		crudService.post(urlChartEndpoint, filter, {onsuccess: function(result) {
			if (onsuccess) {
				onsuccess(result);
			}
		}});
	};
	
	this.getOverview = function(filter, chartData, onsuccess) {
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

	this.getOverviewSettings = function(overviewSettings, onsuccess) {
		crudService.post(urlOverviewSettingsEndpoint, overviewSettings, {onsuccess: function(result) {
			if (onsuccess) {
				onsuccess(result);
			}
		}});
	};
	
}

function LogsRenderer(templateProvider) {

	this.renderDataTable = function($scope, onsuccess) {
		
		var url = '../rest/logs/page';
		var logsUrl = 'logs.html?showList=true&immediate=true';
		var templateLogUrl = '../template/log.html';
		var dataTableSelector = '#dataTable';

		/// DataTable detail
		function renderDetail($scope, $detailContainer, nRow, aData, iDataIndex) {
			$scope.nRow = nRow;
			$scope.aData = aData;
			$scope.iDataIndex = iDataIndex;
			templateProvider.compile(templateLogUrl, $detailContainer, $scope);
		};

		$scope.dataTable = $(dataTableSelector).metalisxDataTable($scope.filter, {
			onsuccess: onsuccess,
			renderDetail: function($detailContainer, nRow, aData, iDataIndex) {
				renderDetail($scope, $detailContainer, nRow, aData, iDataIndex);
			},
			"dataTableSettings": {
				"sAjaxSource": url,
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
		});

	};

	this.renderChart = function($scope, logsService, chartData, onsuccess) {
		
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
					min: $scope.filter.startDate, 
					max: $scope.filter.endDate 
				},
				selection: { 
					mode: "x" 
				}
			},
			onsuccess: function(plot, chartData) {
				plot.getPlaceholder().off("plotselected"); // clean up an eventual previous set event
				plot.getPlaceholder().on("plotselected", function (event, ranges) {
	        		$scope.filter.startDate = $.metalisxUtils.dateToIsoDateAsString(new Date(ranges.xaxis.from));
	        		$scope.filter.endDate = $.metalisxUtils.dateToIsoDateAsString(new Date(ranges.xaxis.to));
	        		$scope.filter.range = 'custom';
	    			$scope.filter.realtime = false;
					$scope.$digest();
					logsService.getChart($scope.filter, function(chartData) {
						$this.renderChart($scope, logsService, chartData);
						if ($scope.filter.showList) {
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
	
	this.renderOverview = function($scope, logsService, plot, overviewData, onsuccess) {

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
					logsService.getOverviewSettings($scope.overviewSettings, function(overviewSettingsData) {
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

}

function LogsController($scope, $compile, $http, templateProvider, logsService, logsRenderer) {

	var logsSelector = '#logs';
	var lineChartContainerSelector = '#lineChartContainer';
	var lineChartOverviewSelector = '#lineChartOverview';

	$scope.overviewSettings = {};
	$scope.timer = null;

	function init() {
		logsService.getFilter(function(result) {
			if (result && result.item) {
				$scope.filter = result.item;
				$scope.$digest();
				renderAll();
			}
		});
	}
	
	function realtimeUpdate() {
		renderAll(function() {
			if ($scope.filter.realtime) {
				$scope.timer = setTimeout(realtimeUpdate, $scope.filter.realtimeInterval);
			}
		});
	}
	
	function renderCharts(onsuccess) {
		if ($scope.filter.showChart) {
			logsService.getChart($scope.filter, function(chartData) {
				if (chartData) {
					$(lineChartContainerSelector).show(); // To show canvas labels the chart has to be visible first.
					logsRenderer.renderChart($scope, logsService, chartData, function(plot, chartData) {
						if ($scope.filter.showOverviewChart) {
							logsService.getOverview($scope.filter, chartData, function(overviewData) {
								if (overviewData) {
									for (prop in $scope.overviewSettings) {
										delete $scope.overviewSettings[prop]; 
									}
									if (overviewData && overviewData.settings) {
										$.extend(true, $scope.overviewSettings, overviewData.settings);
									}
									$(lineChartOverviewSelector).show();
									logsRenderer.renderOverview($scope, logsService, plot, overviewData, function() {
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
		if ($scope.filter.showList) {
			$(logsSelector).show();
			logsRenderer.renderDataTable($scope, onsuccess);
		} else {
			$(logsSelector).hide();
			if (onsuccess) {
				onsuccess();
			}
		}
	};

	function renderAll(onsuccess) {
		if ($scope.filter.showList && $scope.filter.showChart) { // render chart and list after each other is required for proper use of timer
			renderDataTable(function() {
				renderCharts(onsuccess);
			});
		} else if ($scope.filter.showList) {
			renderDataTable(onsuccess);
			renderCharts();
		} else if ($scope.filter.showChart) {
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
	
	$scope.removeRealtimeUpdateTimer = function() {
		if ($scope.timer != null) {
			clearTimeout($scope.timer);
			$scope.timer = null;
		}
	};
	
	$scope.toggleRealtimeChartUpdate = function() {
		if ($scope.filter.realtime) {
			if ($scope.timer == null) {
				realtimeUpdate();
			}
		} else {
			$scope.removeRealtimeUpdateTimer();
		}
	};
	
	$scope.changeRange = function() {
		$scope.filter.startDate = null;
		$scope.filter.endDate = null;
		if ($scope.filter.range == 'custom') {
			$scope.filter.realtime = false;
			$scope.removeRealtimeUpdateTimer();
		}
	};

	$scope.toggleShowCharts = function() {
		renderAll();
	};
	
	$scope.toggleShowList = function() {
		renderAll();
	};
	
	$scope.search = function() {
		renderAll();
	};

	init();

}
