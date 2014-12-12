(function(angular) {
	
	'use strict';

	// Module
	
	var application = angular.module('application');

	// Controller
	
	application.controller('EntityController', 
		function($scope, $window, $routeParams, $controller, crudService, entityCacheService, applicationContext) {
		
			var entityClass = $routeParams.entityClass;
			var restEndpoint =  applicationContext.contextPath + '/rest/entity/' + entityClass;
		
			// Init base controller
			
			function initBaseController() {
				$controller('CrudDetailController', { 
					$scope: $scope, $window: $window, $routeParams: $routeParams, crudService: crudService,
					settings: {
						restEndpoint: restEndpoint,
						getItem: function(data) {
							return data.item;
						}
					}});
			}
			
			// Init
			
			function init() {
				entityCacheService.load($scope, function() {
					initBaseController();
				});
			}
			
			init();
			
	});

})(window.angular);