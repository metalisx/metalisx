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
	
		// logging
		
		$routeProvider
		.when('/logging', {
			templateUrl: contextPath + '/template/logging.html',
			reloadOnSearch: false
		})
		
		// file upload
		
		$routeProvider
		.when('/file-upload', {
			templateUrl: contextPath + '/template/file-upload.html',
			reloadOnSearch: false
		})
		
		// web service
		
		$routeProvider
		.when('/webservice', {
			templateUrl: contextPath + '/template/webservice.html',
			controller: 'WebserviceController',
			reloadOnSearch: false
		})
		
		// input field
		
		$routeProvider
		.when('/input-field', {
			templateUrl: contextPath + '/template/input-field.html',
			controller: 'InputFieldController',
			reloadOnSearch: false
		})
	
		// certificate
		
		$routeProvider
		.when('/certificate', {
			templateUrl: contextPath + '/template/certificate.html',
			reloadOnSearch: false
		})
		
		// alert module
		
		$routeProvider
		.when('/alert', {
			templateUrl: contextPath + '/template/angular-module-alert.html',
			controller: 'AlertController',
			reloadOnSearch: false
		})
		
		// messages module
		
		$routeProvider
		.when('/messages', {
			templateUrl: contextPath + '/template/angular-module-messages.html',
			controller: 'MessagesController',
			reloadOnSearch: false
		})
		
		// block module
		
		$routeProvider
		.when('/block', {
			templateUrl: contextPath + '/template/angular-module-block.html',
			controller: 'BlockController',
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
