(function(angular) {
	
	'use strict';

	// Module
	
	var application = angular.module('application');

	// Controller

	application.controller('BlockModuleController', 
		function BlockModuleController($scope, $timeout) {
		
			$scope.block = function($event) {
				$event.stopPropagation();
				$event.preventDefault();
				$scope.$broadcast('block', $event);
				$
			};
		
		}

	);
	
})(window.angular);
