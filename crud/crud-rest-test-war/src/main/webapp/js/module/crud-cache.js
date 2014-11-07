/**
 * Angular module :: crudCacheService
 * 
 * The Angular module crudCacheService provides the cache for
 * the application.
 */
(function(angular) {
	
	'use strict';

	// Module
	
	var crudCacheService = angular.module('crudCacheService', []);

	// Services
	
	crudCacheService.service('crudCacheService', function(crudService, applicationCache, applicationContext) {
	
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
		
	});

})(window.angular);
