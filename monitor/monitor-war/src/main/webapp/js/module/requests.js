function RequestsController($scope, $compile, $http, $location, $routeParams, 
			crudService, utilsService, applicationContext) {

	//var requestUrl = applicationContext.contextPath + '/page/requests.jsp/request/';
	var requestUrl = '/request/';
	var requestJsonEndpoint = applicationContext.contextPath + '/rest/requests';
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
				"sAjaxSource": crudService.getPageEndpoint(requestJsonEndpoint),
				"aaSorting": [[0, 'desc']],
		        "aoColumns": [
					{ "sName": "startTime",
						"mDataProp": "startTime",
						"fnRender": function ( oObj, sVal ) {
							return $.metalisxUtils.isoDateAsStringtoDisplayDate(sVal);
						}},
					{ "sName": "requestId", 
						"mDataProp": "requestId",
						"fnRender": function ( oObj, sVal ) {
							return "<div class='wordwrap'>" + sVal + "</div>";
						}},
					{ "sName": "duration",
						"mDataProp": "duration",
						"fnRender": function ( oObj, sVal ) {
							return "<div class='wordwrap'>" + sVal + "</div>";
						}},
					{ "sName": "url",
						"mDataProp": "url",
						"fnRender": function ( oObj, sVal ) {
							return "<div class='wordwrap'>" + sVal + "</div>";
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
	}
	
	init();
}
