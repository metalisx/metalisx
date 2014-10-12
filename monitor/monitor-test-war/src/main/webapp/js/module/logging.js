function HierarchyLoggingController($scope, crudService) {

	var resultContainerId = 'resultContainer';
	
	$scope.run = function($event) {
		$event.stopPropagation();
		$event.preventDefault();
		crudService.get('../rest/test/testLoggingHierarchy', null, {onsuccess: function(data) {
			$.metalisxMessages({id: resultContainerId, message: data.item}, {clean : false});
		}});
	};

}

function DomainServiceLoggingController($scope, crudService) {

	var $data = $('#domainServiceLoggingData', $scope.$element);

	$scope.model = {
		date: '2012-11-28T04:12:32.000',
		name: ''
	};

	$scope.add = function($event) {
		$event.stopPropagation();
		$event.preventDefault();
		crudService.post('../rest/test/testDomainServiceLoggingPersist', $scope.model, {onsuccess: function(data) {
			$data.empty();
			$.each(data.items, function(index, item) {
				$data.append(item.name + ' (' + item.date + ')<br/>');
			}); 
		}});
	};
	
	$scope.clean = function($event) {
		$event.stopPropagation();
		$event.preventDefault();
		crudService.del('../rest/test/testDomainServiceLoggingClean', null, {onsuccess: function(data) {
			$data.empty();
			$data.append(data.item);
		}});
	};

};
