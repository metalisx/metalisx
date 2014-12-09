(function(angular) {
	
	'use strict';

	// Module
	
	var application = angular.module('application');

	// Controller
	
	application.controller('CrudDetailController', 
		function CrudDetailController($scope, $timeout, $compile, $location, $window, $routeParams, templateProvider, crudService, 
				crudCacheService, applicationContext) {
		
			var scope = $scope;
			
			$scope.entityClass = null;
			$scope.id = null;
			
			var restEndpoint =  applicationContext.contextPath + '/rest/crud/';
		
			$scope.entity = null;
			
			function init() {
				$scope.entityClass = $routeParams.entityClass;
				$scope.id = $routeParams.id;
				restEndpoint = restEndpoint + $scope.entityClass;
				crudCacheService.load($scope, function() {
					$scope.initEntity();
				});
			}
			
			// Entity
			
			$scope.initEntity = function() {
				var id = $routeParams.id;
				if (id != null) {
					$scope.getEntity(id);
				} else {
					$scope.getNewEntity();
				}
			}
		
			$scope.getEntity = function (id) {
				crudService.get(restEndpoint, {'id': id}, { onsuccess: function(result) {
					$scope.entity = result;
					$scope.$apply();
				}});
			}
			
			$scope.getNewEntity = function() {
				crudService.getNewEntity(restEndpoint, {onsuccess: function(result) {
					if (result && !result.exception && !result.validationerror) {
						$scope.entity = result;
						$scope.$apply();
					}
				}});
			};
		
			// Actions
			
			$scope.save = function($event) {
				$event.stopPropagation();
				$event.preventDefault();
				if ($scope.entity.id == null) {
					$scope.put();
				} else {
					$scope.post();
				}
			}
			
			$scope.put = function() {
				crudService.put(restEndpoint, $scope.entity.item, {onsuccess: function(result) {
					if (result && !result.exception && !result.validationerror) {
						$scope.entity = null;
						$window.history.back();
					}
				}});
			};
		
			$scope.post = function() {
				crudService.post(restEndpoint, $scope.entity.item, {onsuccess: function(result) {
					if (result && !result.exception && !result.validationerror) {
						$window.history.back();
					}
				}});
			};
			
			$scope.cancel = function($event) {
				$event.stopPropagation();
				$event.preventDefault();
				$window.history.back();
			};
		
			init();
			
	});

})(window.angular);