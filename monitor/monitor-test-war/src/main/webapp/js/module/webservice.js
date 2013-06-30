function WebserviceController($scope, crudService) {

	var $resultContainer = $('#resultContainer', $scope.$element);
	
	$scope.model = {
		date: '2012-11-28T04:12:32.000',
		name: 'Enter your name here...'
	};

	$scope.run = function() {
		crudService.post('../rest/test/testWebservice', $scope.model, {onsuccess: function(data) {
			$resultContainer.metalisxAlert(data.item, {clean: false}); 
		}});
	};

}
