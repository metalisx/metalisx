(function(angular) {
	
	'use strict';

	// Module
	
	var messagesController = angular.module('messagesController', []);

	// Controller
	
	messagesController.controller('MessagesController', 
		function MessagesController($scope, crudService) {
		
			$scope.getMessagesDefaultContainer = function($event) {
				$event.stopPropagation();
				$event.preventDefault();
				crudService.get('../rest/test/testMessagesDefaultContainer', $scope.model, {onsuccess: function(data) {
					$.metalisxMessages(data);
				}});
			};
			
			$scope.getSingleMessageNamedContainer = function($event) {
				$event.stopPropagation();
				$event.preventDefault();
				crudService.get('../rest/test/testSingleMessageNamedContainer', $scope.model, {onsuccess: function(data) {
					$.metalisxMessages(data);
				}});
			};
			
			
			$scope.getMessagesNamedContainer = function($event) {
				$event.stopPropagation();
				$event.preventDefault();
				crudService.get('../rest/test/testMessagesNamedContainer', $scope.model, {onsuccess: function(data) {
					$.metalisxMessages(data);
				}});
			};
			
			$scope.getMessagesMultipleNamedContainers = function($event) {
				$event.stopPropagation();
				$event.preventDefault();
				crudService.get('../rest/test/testMessagesMultipleNamedContainers', $scope.model, {onsuccess: function(data) {
					$.metalisxMessages(data);
				}});
			};
			
		}

	);
	
})(window.angular);
