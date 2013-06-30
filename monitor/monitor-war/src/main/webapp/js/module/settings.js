application.service('settingsRenderer', SettingsRenderer);

application.service('settingsService', SettingsService);

function SettingsService(crudService) {

	var saveEndpoint = '../rest/settings/item';
	
	this.save = function(aData, onsuccess) {
		crudService.post(saveEndpoint, aData, {onsuccess: onsuccess});
	};
	
}

function SettingsRenderer(templateProvider) {

	this.renderDataTable = function($scope) {
		
		var url = '../rest/settings/page';
		var templateSettingUrl = '../template/setting.html';
		var dataTableSelector = '#dataTable';

		$scope.refresh = function($detailContainer, nRow, aData, iDataIndex) {
			$scope.nRow = nRow;
			$scope.aData = aData;
			$scope.iDataIndex = iDataIndex;
			templateProvider.compile(templateSettingUrl, $detailContainer, $scope);
		};
		
		$scope.dataTable = $(dataTableSelector).metalisxDataTable(null, {
			renderDetail: function($detailContainer, nRow, aData, iDataIndex) {
				$scope.refresh($detailContainer, nRow, aData, iDataIndex);
			},
			"dataTableSettings": {
				"sAjaxSource": url,
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
		});
	};
	
}

function SettingController($scope, $compile, $http, settingsRenderer, settingsService) {

	$scope.nRow = null;
	$scope.aData = null;
	$scope.iDataIndex = null;
	
	function init() {
		settingsRenderer.renderDataTable($scope);
	}
	
	$scope.save = function() {
		settingsService.save($scope.aData, function(result) {
			$scope.dataTable.fnDraw();
		});
	};
	
	$scope.cancel = function() {
		$scope.dataTable.fnClose($scope.nRow);
	};
	
	init();
}
