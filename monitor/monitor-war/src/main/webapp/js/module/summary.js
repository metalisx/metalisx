function SummaryController($scope, $compile, $http, $location, $routeParams, $timeout,
			crudService, utilsService, applicationContext) {

	var logsUrl = applicationContext.contextPath + '/page/logs.html#/?showList=false&showChart=true&immediate=true';
	var requestJsonEndpoint = applicationContext.contextPath + '/rest/summary';

	$scope.dataTableEnabled = false;
	$scope.dataTableSettings = null;
	
	$scope.dataTableFilterInitial = null;
	$scope.dataTableFilter = {};

	// DataTable
	function initDataTable() {
		
		$scope.dataTableSettings = {
			onsuccessRow: function(nRow, aData, iDataIndex) {
				var html = $(nRow).html();
				$(nRow).html(
					$compile(html)($scope)
				);
			},
			"dataTableSettings": {
				"sAjaxSource": crudService.getPageEndpoint(requestJsonEndpoint),
				"aaSorting": [[0, 'desc']],
		        "aoColumns": [
					{ "sName": "message",
						"mDataProp": "message", 
						"fnRender": function ( oObj, sVal ) {
							return '<a href="' + logsUrl + '&message=' + sVal + '">' + 
										sVal + '</a>';
						}},
					{ "sName": "count",
						"mDataProp": "count"},
					{ "sName": "minDuration",
						"mDataProp": "minDuration"},
					{ "sName": "maxDuration",
						"mDataProp": "maxDuration"},
					{ "sName": "averageDuration",
						"mDataProp": "averageDuration"},
					{ "sName": "totalDuration",
						"mDataProp": "totalDuration"}
				]
			}
		};
	};

	// Init
	
	function init() {
		initDataTable();
		crudService.getFilter(requestJsonEndpoint, {onsuccess: function(result) {
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
		}
	};

	// Helpers
	
	$scope.refreshDataTable = function() {
		$timeout(function() {
			// When it is true then set it to false, so the watch event in the directive is triggered.
			if ($scope.dataTableEnabled === true) {
				$scope.dataTableEnabled = false;
				$scope.$apply();
			}
			$scope.dataTableEnabled = true;
			$scope.$apply();
		});
	}

	// Filter

	function initDataTableFilter() {
		$.extend(true, $scope.dataTableFilter, $scope.dataTableFilterInitial);
		if (!utilsService.isUrlParamEmpty($routeParams.message)) {
			$scope.dataTableFilter.message = $routeParams.message;
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
	}
	
	init();
}
