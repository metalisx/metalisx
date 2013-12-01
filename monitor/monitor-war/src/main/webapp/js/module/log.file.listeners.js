application.service('logFileListenersService', LogFileListenersService);

application.service('logFileListenersModelValidator', LogFileListenersModelValidator);

function LogFileListenersService(crudService) {
	
	var restEndpoint = '../rest/listener';
	var modelRestEndpoint = restEndpoint + '/model';
	var listRestEndpoint = restEndpoint + '/list';
	var startRestEndpoint = restEndpoint + '/start';
	var stopRestEndpoint = restEndpoint + '/stop';
	
	this.getModel = function(onsuccess) {
		crudService.get(modelRestEndpoint, null, {onsuccess: onsuccess});
	};

	this.getList = function(onsuccess) {
		crudService.get(listRestEndpoint, null, {onsuccess: onsuccess, cleanMessagesContainer: false});
	};

	this.start = function(model, onsuccess) {
		crudService.post(startRestEndpoint, model, {onsuccess: onsuccess});
	};

	this.stop = function(model, onsuccess) {
		crudService.post(stopRestEndpoint, model, {onsuccess: onsuccess});
	};

}

function LogFileListenersModelValidator(messagesProvider) {
	
	this.validate = function(model) {
		var isValid = true;
		if (model.filename == null || model.filename == '') {
			messagesProvider.message({message: 'Filename is required, please enter the location of the file on ' +
				'the application server. For example: c:/monitor/log4j/log4j.log.', level: 'error'});
			$('#filenameId').focus();
			isValid = false;
		}
		return isValid;
	};
	
}

function LogFileListenersController($scope, templateProvider, logFileListenersService, logFileListenersModelValidator) {

	function init() {
		logFileListenersService.getModel(function(result) {
			if (result && result.item) {
				$scope.model = result.item;
				$scope.$digest();
				$scope.getList();
			}
		});
	}

	$scope.start = function() {
		if (logFileListenersModelValidator.validate($scope.model)) {
			logFileListenersService.start($scope.model, function(result) {
				$scope.getList();
			});
		}
	};

	$scope.stop = function(index) {
		if (logFileListenersModelValidator.validate($scope.model)) {
			logFileListenersService.stop({filename: $scope.items[index]}, function(result) {
				$scope.getList();
			});
		}
	};

	$scope.getList = function() {
		$fileListeners = $('#fileListeners');
		logFileListenersService.getList(function(result) {
			if (result && result.items) {
				$scope.items = result.items;
				$scope.$digest();
			} else {
				$scope.items = {};
			}
		});
	};
	
	init();

}
