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
				$scope.$broadcast('ngc.block', $event);
				$timeout(function() {
					$scope.$broadcast('ngc.unblock', $event);
				}, 2000);
			};
		
		}

	);
	
})(window.angular);
