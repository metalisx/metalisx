(function(angular) {
	
	'use strict';

	// Module
	
	var application = angular.module('application');

	// Controller

	application.controller('BlockController', 
		function BlockController($scope, $rootScope, $timeout) {
		
			$scope.block = function($event) {
				$event.stopPropagation();
				$event.preventDefault();
				$rootScope.$broadcast('ngc.block');
				$timeout(function() {
					$rootScope.$broadcast('ngc.unblock');
				}, 2000);
			};
		
		}

	);
	
})(window.angular);
