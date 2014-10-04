function LogController($scope, $compile, $http, $location, $window, $routeParams, crudService, 
		applicationContext) {

	var logUrl = applicationContext.contextPath + '/rest/logs/list-item/';
	var resendEndpoint = applicationContext.contextPath + '/rest/logs/log/resend/';

	$scope.entity = null;
	
	// Init
	
	function init() {
		$scope.initEntity();
	}
	
	// Entity
	
	$scope.initEntity = function() {
		var id = $routeParams.id;
		$scope.getEntity(id);
	}

	$scope.getEntity = function(id) {
    	$.metalisxDataProvider.get(logUrl + id, null, {onsuccess: function(result) {
    		$scope.entity = result.item;
    		$scope.$apply();
    	}});
    }

	init();
}
