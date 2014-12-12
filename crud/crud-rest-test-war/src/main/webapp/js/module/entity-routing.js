(function(angular) {
	
	'use strict';

	// Module
	
	var application = angular.module('application');

	// Routings
	
	application.config(function($routeProvider, $locationProvider) {
	
		var contextPath = (typeof $.contextPath != 'undefined' && $.contextPath != null ? $.contextPath : '');
	
		// console
		
		$routeProvider
		.when('/', {
			templateUrl: contextPath + '/template/console.html',
			reloadOnSearch: false
		})
	
		// Entity
		
		$routeProvider
		.when('/entities/:entityClass', {
			templateUrl: contextPath + '/template/entities.html',
			controller: 'EntitiesController',
			reloadOnSearch: false
		})
		
		$routeProvider
		.when('/entity/:entityClass', {
			templateUrl: contextPath + '/template/entity.html',
			controller: 'EntityController',
			reloadOnSearch: false
		})
		
		$routeProvider
		.when('/entity/:entityClass/:id', {
			templateUrl: contextPath + '/template/entity.html',
			controller: 'EntityController',
			reloadOnSearch: false
		})
		
		$routeProvider
		.otherwise({
			redirectTo: redirect
		});
	
		// We use the hashbang otherwise the handeling of pages on the
		// serverside needs to be changed.
		// configure html5 to get links working on jsfiddle
	    // $locationProvider.html5Mode(true);
	});
	
	var redirect = function(skip, url) {
		console.log('RouterProvider -> redirect to: ' + url);
	    window.location = url;
	};
	
})(window.angular);
