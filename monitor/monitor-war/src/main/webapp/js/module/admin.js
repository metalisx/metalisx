function AdminController($scope, alertProvider, crudService) {

	var clearMonitorRequestsUrl = '../rest/admin/requests/clean';
	var clearMonitorLogsUrl = '../rest/admin/logs/clean';
	
	$scope.clearMonitorRequests = function() {
		crudService.get(clearMonitorRequestsUrl, null);
	};
	
	$scope.clearMonitorLogs = function() {
		crudService.get(clearMonitorLogsUrl, null);
	};

}
