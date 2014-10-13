application.config(function($routeProvider, $locationProvider) {

	var contextPath = (typeof $.contextPath != 'undefined' && $.contextPath != null ? $.contextPath : '');

	// console
	
	$routeProvider
	.when('/', {
		templateUrl: contextPath + '/template/console.html',
		reloadOnSearch: false
	})

	// crud
	
	$routeProvider
	.when('/crud-list/:entityClass', {
		templateUrl: contextPath + '/template/crud-list.html',
		controller: CrudListController,
		reloadOnSearch: false
	})
	
	$routeProvider
	.when('/crud-detail/:entityClass', {
		templateUrl: contextPath + '/template/crud-detail.html',
		controller: CrudDetailController,
		reloadOnSearch: false
	})
	
	$routeProvider
	.when('/crud-detail/:entityClass/:id', {
		templateUrl: contextPath + '/template/crud-detail.html',
		controller: CrudDetailController,
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
