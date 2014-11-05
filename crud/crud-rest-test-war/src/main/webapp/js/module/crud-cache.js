application.service('crudColumnValueFormatter', CrudColumnValueFormatter);

function CrudColumnValueFormatter($filter) {
	
	this.format = function(type, value) {
		if (type == 'date') {
			return $filter('ngcDate')(value);
		}
		return value;
	};
	
}

application.service('crudCacheService', CrudCacheService);

function CrudCacheService(crudService, applicationCache, applicationContext) {

	var crudJsonEndpoint = '../rest/crud'

	this.load = function($scope, onsuccess) {
		if (applicationCache.get('entitiesMetadata') === undefined) {
			crudService.metadata(crudJsonEndpoint, {onsuccess: function(result) {
				if (result) {
					applicationCache.put('entitiesMetadata', result);
					$scope.entitiesMetadata = applicationCache.get('entitiesMetadata');
					$scope.$apply();
					if (onsuccess) {
						onsuccess();
					}
				}
			}});
		} else {
			$scope.entitiesMetadata = applicationCache.get('entitiesMetadata');
			if (onsuccess) {
				onsuccess();
			}
		}
	}
	
}
