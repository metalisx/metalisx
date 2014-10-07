function AdminController($scope, crudService) {

	var clearMonitorRequestsUrl = '../rest/admin/requests/clean';
	var clearMonitorLogsUrl = '../rest/admin/logs/clean';
	
	$scope.clearMonitorRequests = function($event) {
		$event.stopPropagation();
		$event.preventDefault();
		crudService.get(clearMonitorRequestsUrl, null);
	};
	
	$scope.clearMonitorLogs = function($event) {
		$event.stopPropagation();
		$event.preventDefault();
		crudService.get(clearMonitorLogsUrl, null);
	};

}
