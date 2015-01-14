(function(angular) {
	
	'use strict';

	// Module
	
	var application = angular.module('application');

	// Controller
	
	application.controller('EntityNavbarController', 
		function($scope, entityCacheService) {
	
			function init() {
				entityCacheService.load($scope);
			}
			
			init();
		
		});

})(window.angular);
