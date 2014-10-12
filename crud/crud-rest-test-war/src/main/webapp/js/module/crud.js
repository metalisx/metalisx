application.service('crudColumnValueFormatter', CrudColumnValueFormatter);

function CrudColumnValueFormatter() {
	
	this.format = function(type, value) {
		if (type == 'date') {
			return $.metalisxUtils.isoDateAsStringtoDisplayDate(value);
		}
		return value;
	};
	
}

function CrudController($scope, $timeout, $compile, templateProvider, crudService, 
		crudCacheService, crudColumnValueFormatter) {

	var scope = $scope;
	
	$scope.entityClass = $.getUrlValue('entityClass');

	var restEndpoint = '../rest/crud/' + $scope.entityClass;
	var templateDetailUrl = '../template/crud-detail.html';
	var dataTableSelector = '[id="' + $scope.entityClass + '.dataTable"]';

	$scope.newEntity = null;
	$scope.entity = null;
	
	function init() {
		crudCacheService.load($scope);
	}
	
	$scope.getNewEntity = function() {
		crudService.getNewEntity(restEndpoint, {onsuccess: function(data) {
			$scope.newEntity = data;
			$scope.$apply();
		}});
	};

	$scope.getDataTable = function() {
		crudService.pageMetadata(restEndpoint, {onsuccess: function(data) {

			var aaSorting = new Array();
			var aoColumns = new Array();
			
			if (data.sorting) {
				$.each(data.sorting, function(index, sort) {
					aaSorting[aaSorting.length] = sort;
				});
			}
			if (data.columns) {
				$.each(data.columns, function(index , column) {
					aoColumns[aoColumns.length] = {
						sName: column.field.name,
						mDataProp: column.field.name,
						sTitle: column.title,
						sClass: column.className,
						bSortable: column.sortable,
						fnRender: function(oObj, sVal) {
							return crudColumnValueFormatter.format(column.field.type, sVal);
						}
					};
				});
				aoColumns[aoColumns.length] = {
					sTitle: null,
					mDataProp: null,
					sClass: "delete",
					bSortable: false,
					fnRender: function (oObj, sVal ) {
						return '<a href="#" ng-click="del(\'' + oObj.aData.id + '\', $event)">X</a>';
					}
				};
			}
			
			$scope.showDataTableDetail = function($detailContainer, nRow, aData, iDataIndex) {
				$scope.nRow = nRow;
				$scope.iDataIndex = iDataIndex;
				crudService.get(restEndpoint, {"id": aData.id}, {onsuccess: function(data) {
					scope.entity = data;
					templateProvider.compile(templateDetailUrl, $detailContainer, scope);
				}});
			};
			
			$scope.dataTable = $(dataTableSelector).metalisxDataTable(null, {
				renderDetail: function($detailContainer, nRow, aData, iDataIndex) {
					$scope.showDataTableDetail($detailContainer, nRow, aData, iDataIndex);
				},
				"dataTableSettings": {
					"sAjaxSource": crudService.getPageEndpoint(restEndpoint),
					"aaSorting": aaSorting,
			        "aoColumns": aoColumns
				},
				onsuccessRow: function(nRow, aData, iDataIndex) {
					var html = $(nRow).html();
					$(nRow).html(
						$compile(html)($scope)
					);
				}
			});
		}});
	};

	$scope.put = function() {
		crudService.put(restEndpoint, $scope.newEntity.item, {onsuccess: function(data) {
			$scope.newEntity = null;
			$scope.$apply();
			$scope.dataTable.fnDraw();
		}});
	};

	$scope.post = function() {
		crudService.post(restEndpoint, $scope.entity.item, {onsuccess: function(data) {
			$scope.entity = null;
			$scope.dataTable.fnDraw();
		}});
	};
	
	$scope.del = function(id, $event) {
		crudService.del(restEndpoint, {"id": id}, {onsuccess: function(data) {
			$scope.entity = null;
			$scope.dataTable.fnDraw();
		}});
		$event.stopPropagation();
	};
	
	$scope.cancel = function() {
		$scope.entity = null;
		$scope.dataTable.fnClose($scope.nRow);
	};

	$scope.cancelNewEntity = function() {
		$scope.newEntity = null;
	};

	$timeout(function() {
		$scope.getDataTable();
	}, 0);

	init();
	
}
