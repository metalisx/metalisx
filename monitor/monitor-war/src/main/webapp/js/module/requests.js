function RequestsController($scope, $compile, $http, $location, $routeParams, $timeout,
			crudService, utilsService, applicationContext) {

	var requestUrl = '/requests/request/';
	var requestJsonEndpoint = applicationContext.contextPath + '/rest/requests';

	$scope.dataTableEnabled = false;
	$scope.dataTableSettings = null;
	
	$scope.dataTableFilterInitial = null;
	$scope.dataTableFilter = {};

	// DataTable
	function initDataTable() {
		
		$scope.dataTableSettings = {
			onRowClick: function(nRow, aData, iDataIndex) {
				$scope.$apply(function() {
					$location.path(requestUrl + aData.id);
				});
			},
			onsuccessRow: function(nRow, aData, iDataIndex) {
				var html = $(nRow).html();
				$(nRow).html(
					$compile(html)($scope)
				);
			},
			"dataTableSettings": {
				"ajaxSource": crudService.getPageEndpoint(requestJsonEndpoint),
				"sorting": [[0, 'desc']],
		        "columns": [
					{ "name": "startTime",
						"data": "startTime",
						"render": function (data, type, full, meta) {
							var logDate = data == null ? '' : $.metalisxUtils.isoDateAsStringtoDisplayDate(data);
							return logDate;
						}},
					{ "name": "requestId", 
						"data": "requestId",
						"render": function (data, type, full, meta) {
							var requestId = data == null ? '' : data;
							return "<div class='wordwrap'>" + requestId + "</div>";
						}},
					{ "name": "duration",
						"data": "duration",
						"render": function (data, type, full, meta) {
							var duration = data == null ? '' : data;
							return "<div class='wordwrap'>" + duration + "</div>";
						}},
					{ "name": "url",
						"data": "url",
						"render": function (data, type, full, meta) {
							var url = data == null ? '' : data;
							return "<div class='wordwrap'>" + url + "</div>";
						}}
				]
			}
		};
	};

	// Init
	
	function init() {
		initDataTable();
		crudService.getFilter(requestJsonEndpoint, {onsuccess: function(result) {
			$scope.dataTableFilterInitial = result.item;
			$.extend(true, $scope.dataTableFilter, $scope.dataTableFilterInitial);
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
		var $params = $location.search();
		if ($.isEmptyObject($params)) {
			$.extend(true, $scope.dataTableFilter, $scope.dataTableFilterInitial);
		} else {
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
		}
	}
	
	init();
}
