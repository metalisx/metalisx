function MessagesController($scope, crudService) {

	$scope.getMessagesDefaultContainer = function() {
		crudService.get('../rest/test/testMessagesDefaultContainer', $scope.model, {onsuccess: function(data) {
			$.metalisxMessages(data);
		}});
	};
	
	$scope.getSingleMessageNamedContainer = function() {
		crudService.get('../rest/test/testSingleMessageNamedContainer', $scope.model, {onsuccess: function(data) {
			$.metalisxMessages(data);
		}});
	};
	
	
	$scope.getMessagesNamedContainer = function() {
		crudService.get('../rest/test/testMessagesNamedContainer', $scope.model, {onsuccess: function(data) {
			$.metalisxMessages(data);
		}});
	};
	
	$scope.getMessagesMultipleNamedContainers = function() {
		crudService.get('../rest/test/testMessagesMultipleNamedContainers', $scope.model, {onsuccess: function(data) {
			$.metalisxMessages(data);
		}});
	};
	
};
