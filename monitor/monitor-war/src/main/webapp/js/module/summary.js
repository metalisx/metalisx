application.service('summaryService', SummaryService);

application.service('summaryRenderer', SummaryRenderer);

function SummaryService(crudService) {
	
	var filterEndpoint = '../rest/summary/filter';

	this.getFilter = function(onsuccess) {
		crudService.post(filterEndpoint, $.urlValuesToObject(), {onsuccess: onsuccess});
	};

}

function SummaryRenderer() {
	
	this.renderDataTable = function($scope) {
		
		var url = '../rest/summary/page';
		var logsUrl = 'logs.html?showList=false&showChart=true&immediate=true';
		var dataTableSelector = '#dataTable';
		
		$scope.dataTable = $(dataTableSelector).metalisxDataTable($scope.filter, {
			"dataTableSettings": {
				"sAjaxSource": url,
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
		});

	};
	
}

function SummaryController($scope, templateProvider, summaryService, summaryRenderer) {

	function init() {
		summaryService.getFilter(function(result) {
			if (result && result.item) {
				$scope.filter = result.item;
				$scope.$digest();
				$scope.search();
			}
		});
	}

	$scope.changeRange = function() {
		$scope.filter.startDate = null;
		$scope.filter.endDate = null;
	};

	$scope.search = function() {
		summaryRenderer.renderDataTable($scope);
	};

	init();

}
