function InputFieldController($scope, crudService) {

	var resultContainerId = 'resultContainer';
	
	$scope.model = {
		date: '2012-11-28T04:12:32.000',
		name: 'Enter your name here...'
	};
	
	$scope.get = function() {
		crudService.get('../rest/test/get', $scope.model, {onsuccess: function(data) {
			$.metalisxMessages({id: resultContainerId, message: data.item}, {clean : false});
		}});
	};
	
	$scope.put = function() {
		crudService.put('../rest/test/put', $scope.model, {onsuccess: function(data) {
			$.metalisxMessages({id: resultContainerId, message: data.item}, {clean : false});
		}});
	};

	$scope.del = function() {
		var endpoint = '../rest/test/delete/' + $scope.model.date + '/' + $scope.model.name;
		crudService.del(endpoint, null, {onsuccess: function(data) { 
			$.metalisxMessages({id: resultContainerId, message: data.item}, {clean : false});
		}});
	};

	$scope.post = function() {
		crudService.post('../rest/test/post', $scope.model, {onsuccess: function(data) { 
			$.metalisxMessages({id: resultContainerId, message: data.item}, {clean : false});
		}});
	};

	$scope.head = function() {
		$.metalisxDataProvider.head('../rest/test/head', $scope.model, {
			onsuccess: function(data, jqxhr) { 
				var result = jqxhr.getResponseHeader('headresult');
				$.metalisxMessages({id: resultContainerId, message: result}, {clean : false});
			}
		});
	};

	$scope.form = function() {
		$.metalisxDataProvider.form('../rest/test/form', $scope.model, {
			onsuccess: function(data) { 
				$.metalisxMessages({id: resultContainerId, message: data.item}, {clean : false});
			}
		});
	};
	
};
