function CrudListController($scope, $compile, $location, $routeParams, $timeout, templateProvider, crudService, 
		crudCacheService, crudColumnValueFormatter, applicationContext) {

	$scope.entityClass = null;

	var crudDetailUrl = '/crud-detail/';
	var restEndpoint = applicationContext.contextPath + '/rest/crud/';

	$scope.dataTableEnabled = false;
	$scope.dataTableSettings = null;

	$scope.entity = null;
	
	// DataTable
	function initDataTable() {
		crudService.pageMetadata(restEndpoint, {onsuccess: function(data) {

			var sorting = new Array();
			var columns = new Array();
			
			if (data.sorting) {
				$.each(data.sorting, function(index, sort) {
					sorting[sorting.length] = sort;
				});
			}
			if (data.columns) {
				$.each(data.columns, function(index , column) {
					// The clob field are not included in the dataTable.
					if (column.field.isLob == false) {
						columns[columns.length] = {
							name: column.field.name,
							data: column.field.name,
							title: column.title,
							sortable: column.sortable,
							render: function(data, type, full, meta) {
								return crudColumnValueFormatter.format(column.field.type, data);
							}
						};
					}
				});
				columns[columns.length] = {
					data: null,
					sortable: false,
					render: function (data, type, full, meta) {
						return '<a href="#" ng-click="del(\'' + data.id + '\', $event)">X</a>';
					}
				};
			}
			
			$scope.dataTableSettings = {
				onRowClick: function(nRow, aData, iDataIndex) {
					$scope.$apply(function() {
						$location.path(crudDetailUrl + '/' + aData.id);
					});
				},
				onsuccessRow: function(nRow, aData, iDataIndex) {
					var html = $(nRow).html();
					$(nRow).html(
						$compile(html)($scope)
					);
				},
				"dataTableSettings": {
					"ajaxSource": crudService.getPageEndpoint(restEndpoint),
					"sorting": sorting,
			        "columns": columns
				}
			};
			
			$scope.dataTableEnabled = true;
			$scope.$apply();

		}});
	};

	function init() {
		$scope.entityClass = $routeParams.entityClass;
		crudDetailUrl = crudDetailUrl + $scope.entityClass;
		restEndpoint = restEndpoint + $scope.entityClass;
		crudCacheService.load($scope, function onsuccess() {
			initDataTable();
		});
	}
	
	$scope.$on('$routeUpdate', function(next, current) {
		$scope.refreshDataTable();
	});
	
	// Actions
	
	$scope.del = function(id, $event) {
		$event.stopPropagation();
		$event.preventDefault();
		crudService.del(restEndpoint, {"id": id}, {onsuccess: function(data) {
			$scope.refreshDataTable();
		}});
	};
	
	$scope.newEntity = function($event) {
		$event.stopPropagation();
		$event.preventDefault();
		$location.path(crudDetailUrl);
	}
	
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

	init();
	
}
