(function(angular) {
	
	'use strict';

	// Module
	
	var crudNavbarController = angular.module('crudNavbarController', []);

	// Controller
	
	crudNavbarController.controller('CrudNavbarController', 
		function($scope, $timeout, $compile, templateProvider, crudCacheService) {
	
			function init() {
				crudCacheService.load($scope);
			}
			
			init();
		
		});

})(window.angular);
