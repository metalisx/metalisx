(function(angular) {
	
	'use strict';

	// Module
	
	var webserviceController = angular.module('webserviceController', []);

	// Controller
	
	webserviceController.controller('WebserviceController', 
		function WebserviceController($scope, crudService) {
		
			var resultContainerId = 'resultContainer';
			
			$scope.model = {
				date: '2012-11-28T04:12:32.000',
				name: 'Enter your name here...'
			};
		
			$scope.run = function($event) {
				$event.stopPropagation();
				$event.preventDefault();
				crudService.post('../rest/test/testWebservice', $scope.model, {onsuccess: function(data) {
					$.metalisxMessages({id: resultContainerId, message: data.item}, {clean : false});
				}});
			};
		
		}

	);
	
})(window.angular);
