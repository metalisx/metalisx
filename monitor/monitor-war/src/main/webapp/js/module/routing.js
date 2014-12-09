/**
 * Angular module :: ngcApplicationRouting
 * 
 * The Angular module ngcApplicationRouting provides the routing
 * of the application.
 */
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
	
		// admin
		
		$routeProvider
		.when('/admin', {
			templateUrl: contextPath + '/template/admin.html',
			controller: 'AdminController',
			reloadOnSearch: false
		})
		
		// log file listeners
		$routeProvider
		.when('/log-file-listeners', {
			templateUrl: contextPath + '/template/log-file-listeners.html',
			controller: 'LogFileListenersController',
			reloadOnSearch: false
		})
	
		// log file load 
		$routeProvider
		.when('/log-file-load', {
			templateUrl: contextPath + '/template/log-file-load.html',
			controller: 'LogFileLoadController',
			reloadOnSearch: false
		})
	
		// logs
		$routeProvider
		.when('/logs', {
			templateUrl: contextPath + '/template/logs.html',
			controller: 'LogsController',
			reloadOnSearch: false
		})
		.when('/logs/log/:id', {
			templateUrl: contextPath + '/template/log.html',
			controller: 'LogController',
			reloadOnSearch: false
		})
		.when('/logs/log-request/:requestId', {
			templateUrl: contextPath + '/template/log-request.html',
			controller: 'LogRequestController',
			reloadOnSearch: false
		})
	
		// requests
		$routeProvider
		.when('/requests', {
			templateUrl: contextPath + '/template/requests.html',
			controller: 'RequestsController',
			reloadOnSearch: false
		})
		.when('/requests/request/:id', {
			templateUrl: contextPath + '/template/request.html',
			controller: 'RequestController',
			reloadOnSearch: false
		})
		
		// settings
		$routeProvider
		.when('/settings', {
			templateUrl: contextPath + '/template/settings.html',
			controller: 'SettingsController',
			reloadOnSearch: false
		})
		
		$routeProvider
		.when('/summary', {
			templateUrl: contextPath + '/template/summary.html',
			controller: 'SummaryController',
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
