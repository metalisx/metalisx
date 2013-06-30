application.service('logFileLoadService', LogFileLoadService);

application.service('logFileLoadModelValidator', LogFileLoadModelValidator);

function LogFileLoadService(crudService) {
	
	var restEndpoint = '../rest/load';
	var modelRestEndpoint = restEndpoint + '/model';
	var startRestEndpoint = restEndpoint + '/start';

	this.get = function(onsuccess) {
		crudService.get(modelRestEndpoint, null, {onsuccess: onsuccess});
	};

	this.start = function(model) {
		crudService.post(startRestEndpoint, model);
	};

}

function LogFileLoadModelValidator(alertProvider) {
	
	this.validate = function(model) {
		var isValid = true;
		if (model.filename == null || model.filename == '') {
			alertProvider.alert('Filename is required, please enter the location of the file on ' +
				'the application server. For example: c:/monitor/log4j/log4j.log.', {type: 'error'});
			$('#filenameId').focus();
			isValid = false;
		}
		return isValid;
	};
	
}

function LogFileLoadController($scope, alertProvider, templateProvider, logFileLoadService, logFileLoadModelValidator) {

	function init() {
		logFileLoadService.get(function(result) {
			if (result && result.item) {
				$scope.model = result.item;
				$scope.$digest();
			}
		});
	}

	$scope.start = function() {
		if (logFileLoadModelValidator.validate($scope.model)) {
			logFileLoadService.start($scope.model);
		}
	};

	init();

}
