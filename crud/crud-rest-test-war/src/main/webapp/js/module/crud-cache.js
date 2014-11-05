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

function CrudCacheService(crudService, applicationCache, applicationContext, translationService) {

	var crudJsonEndpoint = '../rest/crud'

	// Store translation mappings of readable names for the class names.
	translationService.put({'code':'org.metalisx.crud.domain.model.Test', 'text':'Test'});
	translationService.put({'code':'org.metalisx.crud.domain.model.TestDocument', 'text':'Test document'});
	translationService.put({'code':'org.metalisx.crud.domain.model.TestTextarea', 'text':'Test textarea'});
	
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
