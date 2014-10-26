function SettingsController($scope, $compile, $http, $location, $routeParams,
			templateProvider, crudService, utilsService, applicationContext) {

	var saveEndpoint = '../rest/settings/item';
	var settingJsonEndpoint = applicationContext.contextPath + '/rest/settings';
	var templateSettingUrl = applicationContext.contextPath  + '/template/setting.html';

	$scope.dataTable = null;
	
	$scope.dataTableEnabled = true;
	$scope.dataTableSettings = null;
	$scope.dataTableCallback = null;
	
	$scope.nRow = null;
	$scope.aData = null;
	$scope.iDataIndex = null;

	// DataTable
	function initDataTable() {
		
		$scope.dataTableCallback = function(plot, result) {
			$scope.dataTable = plot;
		}
		
		$scope.refresh = function($detailContainer, nRow, aData, iDataIndex) {
			$scope.nRow = nRow;
			$scope.aData = aData;
			$scope.iDataIndex = iDataIndex;
			templateProvider.compile(templateSettingUrl, $detailContainer, $scope);
		};
		
		$scope.dataTableSettings = {
			renderDetail: function($detailContainer, nRow, aData, iDataIndex) {
				$scope.refresh($detailContainer, nRow, aData, iDataIndex);
			},
			"dataTableSettings": {
				"ajax": crudService.getPageEndpoint(settingJsonEndpoint),
				"sorting": [[1, 'asc']],
		        "columns": [
					{ "name": "id",
						"data": "id"},
					{ "name": "code",
						"data": "code"},
					{ "name": "value",
						"data": "value"}
				]
			}
		};
	};

	// Init
	
	function init() {
		initDataTable();
	}
	
	// Actions
	
	$scope.save = function($event) {
		$event.stopPropagation();
		$event.preventDefault();
		crudService.post(saveEndpoint, $scope.aData, {onsuccess: function(result) {
			$scope.refreshDataTable();
		}});
	};
	
	$scope.cancel = function($event) {
		$event.stopPropagation();
		$event.preventDefault();
		$scope.dataTable.fnClose($scope.nRow);
	};
	
	// Helpers
	
	$scope.refreshDataTable = function() {
		// When it is true then set it to false, so the watch event in the directive is triggered.
		if ($scope.dataTableEnabled === true) {
			$scope.dataTableEnabled = false;
			$scope.$apply();
		}
		$scope.dataTableEnabled = true;
		$scope.$apply();
	}

	init();
}
