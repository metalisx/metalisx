application.config(function($routeProvider, $locationProvider) {

	var contextPath = (typeof $.contextPath != 'undefined' && $.contextPath != null ? $.contextPath : '');

	$routeProvider
	.when('/', {
		templateUrl: contextPath + '/template/logs.html',
		controller: LogsController,
		reloadOnSearch: false
	})
	.when('/log/:id', {
		templateUrl: contextPath + '/template/log.html',
		controller: LogController
	})
	.when('/log-request/:requestId', {
		templateUrl: contextPath + '/template/log-request.html',
		controller: LogRequestController
	})
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
