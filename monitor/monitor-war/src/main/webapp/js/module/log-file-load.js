function LogFileLoadController($scope, crudService) {

	var restEndpoint = '../rest/load';
	var modelRestEndpoint = restEndpoint + '/model';
	var startRestEndpoint = restEndpoint + '/start';

	// Init
	
	function init() {
		crudService.get(modelRestEndpoint, null, {onsuccess: function(result) {
			if (result && result.item) {
				$scope.model = result.item;
				$scope.$digest();
			}
		}});
	}

	// Actions
	
	$scope.start = function($event) {
		$event.stopPropagation();
		$event.preventDefault();
		if (validate($scope.model)) {
			crudService.post(startRestEndpoint, $scope.model);
		}
	};

	// Validator

	function validate(model) {
		var isValid = true;
		if (model.filename == null || model.filename == '') {
			messagesProvider.message({message: 'Filename is required, please enter the location of the file on ' +
				'the application server. For example: c:/monitor/log4j/log4j.log.', level: 'error'});
			$('#filenameId').focus();
			isValid = false;
		}
		return isValid;
	};
	
	init();

}
