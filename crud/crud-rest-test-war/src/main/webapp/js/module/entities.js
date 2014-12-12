(function(angular) {
	
	'use strict';

	// Module
	
	var application = angular.module('application');

	application.controller('EntitiesController', 
		function($scope, $compile, $location, $routeParams, $timeout, $controller, $filter, 
				templateProvider, 
				crudService, 
				entityCacheService, applicationContext) {
		
			var entityClass = $routeParams.entityClass;
			var crudDetailUrl = '/entity/' + entityClass;
			var restEndpoint = applicationContext.contextPath + '/rest/entity/' + entityClass;

			// Init base controller
			
			function initBaseController() {
				$controller('CrudListController', { 
					$scope: $scope, $compile: $compile, $timeout: $timeout, $location: $location,
					crudService: crudService,
					settings: {
						restEndpoint: restEndpoint,
						crudDetailUrl: crudDetailUrl,
						getSorting: getSorting,
						getColumns: getColumns,
						renderDeleteColumn: true
					}});
			}

			// DataTable
			
			function getSorting(data) {
				var sorting = new Array();
				if (data.sorting) {
					$.each(data.sorting, function(index, sort) {
						sorting[sorting.length] = sort;
					});
				}
				return sorting;
			}
			
			function getColumns(data) {
				var columns = new Array();
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
									return $filter('ngcGenericFormatter')(column.field.type, data);
								}
							};
						}
					});
				}
				return columns;
			}

			// Init
			
			function init() {
				entityCacheService.load($scope, function onsuccess() {
					initBaseController();
				});
			}

			init();
			
	});

})(window.angular);
