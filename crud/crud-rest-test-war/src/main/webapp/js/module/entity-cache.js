/**
 * Angular module :: entityCrudCacheService
 * 
 * The Angular module entityCrudCacheService provides the cache for
 * the application.
 */
(function(angular) {
	
	'use strict';

	// Module
	
	var application = angular.module('application');

	// Services
	
	application.service('entityCacheService', function(crudService, applicationCache, applicationContext) {
	
		var crudJsonEndpoint = applicationContext.contextPath + '/rest/entity'
	
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
		
	});

})(window.angular);
