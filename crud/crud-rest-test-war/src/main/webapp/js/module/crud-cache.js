application.service('crudCacheService', CrudCacheService);

function CrudCacheService(crudService, applicationCache, applicationContext) {

	var crudJsonEndpoint = '../rest/crud'

//	var entitieTitles = [
//	                   {'org.metalisx.crud.domain.model.User': 'Users'},
//	                   {'org.metalisx.crud.domain.model.Company': 'Companies'}
//	                   ];

		
	this.load = function($scope, onsuccess) {
		if (applicationCache.get('entitiesMetadata') === undefined) {
			crudService.metadata(crudJsonEndpoint, {onsuccess: function(result) {
				if (result) {
					applicationCache.put('entitiesMetadata', result);
					console.log($scope.entitiesMetadata);
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
