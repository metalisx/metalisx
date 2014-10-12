function InputFieldController($scope, crudService) {

	var resultContainerId = 'resultContainer';
	
	$scope.model = {
		date: '2012-11-28T04:12:32.000',
		name: 'Enter your name here...'
	};
	
	$scope.get = function($event) {
		$event.stopPropagation();
		$event.preventDefault();
		crudService.get('../rest/test/get', $scope.model, {onsuccess: function(data) {
			$.metalisxMessages({id: resultContainerId, message: data.item}, {clean : false});
		}});
	};
	
	$scope.put = function($event) {
		$event.stopPropagation();
		$event.preventDefault();
		crudService.put('../rest/test/put', $scope.model, {onsuccess: function(data) {
			$.metalisxMessages({id: resultContainerId, message: data.item}, {clean : false});
		}});
	};

	$scope.del = function($event) {
		$event.stopPropagation();
		$event.preventDefault();
		var endpoint = '../rest/test/delete/' + $scope.model.date + '/' + $scope.model.name;
		crudService.del(endpoint, null, {onsuccess: function(data) { 
			$.metalisxMessages({id: resultContainerId, message: data.item}, {clean : false});
		}});
	};

	$scope.post = function($event) {
		$event.stopPropagation();
		$event.preventDefault();
		crudService.post('../rest/test/post', $scope.model, {onsuccess: function(data) { 
			$.metalisxMessages({id: resultContainerId, message: data.item}, {clean : false});
		}});
	};

	$scope.head = function($event) {
		$event.stopPropagation();
		$event.preventDefault();
		$.metalisxDataProvider.head('../rest/test/head', $scope.model, {
			onsuccess: function(data, jqxhr) { 
				var result = jqxhr.getResponseHeader('headresult');
				$.metalisxMessages({id: resultContainerId, message: result}, {clean : false});
			}
		});
	};

	$scope.form = function($event) {
		$event.stopPropagation();
		$event.preventDefault();
		$.metalisxDataProvider.form('../rest/test/form', $scope.model, {
			onsuccess: function(data) { 
				$.metalisxMessages({id: resultContainerId, message: data.item}, {clean : false});
			}
		});
	};
	
};
