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
				"sAjaxSource": crudService.getPageEndpoint(settingJsonEndpoint),
				"aaSorting": [[1, 'asc']],
		        "aoColumns": [
					{ "sName": "id",
						"mDataProp": "id",
						"sClass": "id"},
					{ "sName": "code",
						"mDataProp": "code",
						"sClass": "code"},
					{ "sName": "value",
						"mDataProp": "value",
						"sClass": "value"}
				]
			}
		};
	};

	// Init
	
	function init() {
		initDataTable();
	}
	
	// Actions
	
	$scope.save = function() {
		crudService.post(saveEndpoint, $scope.aData, {onsuccess: function(result) {
			$scope.dataTable.fnClose($scope.nRow);
			$scope.refreshDataTable();
		}});
	};
	
	$scope.cancel = function() {
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
