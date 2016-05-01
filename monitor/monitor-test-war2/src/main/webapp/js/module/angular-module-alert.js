(function(angular) {
	
	'use strict';

	// Module
	
	var application = angular.module('application');

	// Controller
	
	application.controller('AlertController', 
		function AlertController($scope, $rootScope, crudService) {
		
			$scope.getAlertAsString = function($event) {
				$event.stopPropagation();
				$event.preventDefault();
				var alert = "hello info passed as a string";
				$rootScope.$broadcast('ngc.alert', alert);
			};
			
			$scope.getAlert = function($event) {
				$event.stopPropagation();
				$event.preventDefault();
				var alert = {"message": "hello info", "level": "info"};
				$rootScope.$broadcast('ngc.alert', alert);
			};
			
			$scope.getAlerts = function($event) {
				$event.stopPropagation();
				$event.preventDefault();
				var alerts = [{"message": "hello error", "level": "error"},
				              {"message": "hello info", "level": "info"},
				              {"message": "hello success", "level": "success"}];
				$rootScope.$broadcast('ngc.alert', alerts);
			};
			
			$scope.getAlertWithId = function($event) {
				$event.stopPropagation();
				$event.preventDefault();
				var alert = {"id": "alertWithIdContainer", "message": "hello info with id", "level": "info"};
				$rootScope.$broadcast('ngc.alert', alert);
			};
			
			$scope.getAlertsWithId = function($event) {
				$event.stopPropagation();
				$event.preventDefault();
				var alerts = [{"id": "alertsWithIdContainer", "message": "hello error with id", "level": "error"},
				              {"id": "alertsWithIdContainer", "message": "hello info with id", "level": "info"},
				              {"id": "alertsWithIdContainer", "message": "hello success with id", "level": "success"}];
				$rootScope.$broadcast('ngc.alert', alerts);
			};
			
			$scope.getAlertsWithDifferentIds = function($event) {
				$event.stopPropagation();
				$event.preventDefault();
				var alerts = [{"id": "alertsWithIdContainerA", "message": "hello error with id for left container", "level": "error"},
				              {"id": "alertsWithIdContainerA", "message": "hello info with id for left container", "level": "info"},
				              {"id": "alertsWithIdContainerB", "message": "hello success with id for right container", "level": "success"}];
				$rootScope.$broadcast('ngc.alert', alerts);
			};
			
			$scope.getAlertsWithAndWithoutId = function($event) {
				$event.stopPropagation();
				$event.preventDefault();
				var alerts = [{"message": "hello info without id", "level": "info"},
				              {"id": "alertsWithAndWithoutIdContainer", "message": "hello success with id", "level": "success"}];
				$rootScope.$broadcast('ngc.alert', alerts);
			};
			
			$scope.getAlertsWithDetail = function($event) {
				$event.stopPropagation();
				$event.preventDefault();
				var alerts = [
				              {"id": "alertsWithDetailContainer", "message": "hello success with detail text as plain text", "level": "success", 
				            	  "detail": "<b>Plain text detail</b><br/>Somet text.     Some more text"},
				              {"id": "alertsWithDetailContainer", "message": "hello success with detail text as HTML", "level": "success", 
				            	  "detail": "<html><body><b>HTML detail</b><br/>Some html text.<br/>Some more html text.</body></html>"},
			            	  {"id": "alertsWithDetailContainer", "message": "hello success without detail", "level": "success", 
				            	  "detail": null}];
				$rootScope.$broadcast('ngc.alert', alerts);
			};
			
		}

	);
	
})(window.angular);
