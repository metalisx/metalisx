(function(angular) {
	
	'use strict';

	// Module
	
	var application = angular.module('application');

	// Controller
	
	application.controller('CrudNavbarController', 
		function($scope, $timeout, $compile, templateProvider, crudCacheService) {
	
			function init() {
				crudCacheService.load($scope);
			}
			
			init();
		
		});

})(window.angular);
