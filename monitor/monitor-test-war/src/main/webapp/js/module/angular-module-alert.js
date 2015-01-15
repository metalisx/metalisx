(function(angular) {
	
	'use strict';

	// Module
	
	var application = angular.module('application');

	// Controller
	
	application.controller('AlertController', 
		function AlertController($scope, $rootScope, crudService) {
		
			$scope.getAlertDefaultContainer = function($event) {
				$event.stopPropagation();
				$event.preventDefault();
				var alerts = [
				              {"id": null, "message": "hello error", "level": "error", "detail": null},
				              {"id": null, "message": "hello info", "level": "info"},
				              {"message": "hello success", "level": "success"}];
				$rootScope.$broadcast('ngc.alert', alerts);
			};
			
			$scope.getSingleAlertTargetContainer = function($event) {
				$event.stopPropagation();
				$event.preventDefault();
				var alert = {"id": "singleAlertContainer", "message": "hello info", "level": "info", "detail": null};
				$rootScope.$broadcast('ngc.alert', alert);
			};
			
			
			$scope.getAlertTargetContainer = function($event) {
				$event.stopPropagation();
				$event.preventDefault();
				crudService.get('../rest/test/testAlertTargetContainer', $scope.model, {onsuccess: function(data) {
					$rootScope.$broadcast('ngc.alert', data);
				}});
			};
			
			$scope.getAlertMultipleTargetContainers = function($event) {
				$event.stopPropagation();
				$event.preventDefault();
				crudService.get('../rest/test/testAlertMultipleTargetContainers', $scope.model, {onsuccess: function(data) {
					$rootScope.$broadcast('ngc.alert', data);
				}});
			};
			
		}

	);
	
})(window.angular);
