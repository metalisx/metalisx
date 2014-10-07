function LogFileListenersController($scope, crudService) {

	var restEndpoint = '../rest/listener';
	var modelRestEndpoint = restEndpoint + '/model';
	var listRestEndpoint = restEndpoint + '/list';
	var startRestEndpoint = restEndpoint + '/start';
	var stopRestEndpoint = restEndpoint + '/stop';

	$scope.model = null;
	
	// Init
	
	function init() {
		console.log('abb');
		crudService.get(modelRestEndpoint, null, {onsuccess: function(result) {
			console.log('c');
			if (result && result.item) {
				$scope.model = result.item;
				$scope.$digest();
				$scope.getList();
			}
		}});
	}

	// Entity
	
	$scope.getList = function() {
		crudService.get(listRestEndpoint, null, {onsuccess: function(result) {
			if (result && result.items) {
				$scope.items = result.items;
				$scope.$digest();
			} else {
				$scope.items = {};
			}
		}});
	};
	
	// Actions
	
	$scope.start = function($event) {
		$event.stopPropagation();
		$event.preventDefault();
		if (validate($scope.model)) {
			crudService.post(startRestEndpoint, $scope.model, {onsuccess: function(result) {
				$scope.getList();
			}});
		}
	};

	$scope.stop = function($event, index) {
		$event.stopPropagation();
		$event.preventDefault();
		if (validate($scope.model)) {
			crudService.post(stopRestEndpoint, {filename: $scope.items[index]}, {onsuccess: function(result) {
				$scope.getList();
			}});
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
