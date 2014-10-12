function CrudMenuController($scope, $timeout, $compile, templateProvider, 
		crudCacheService) {

	function init() {
		crudCacheService.load($scope);
	}
	
	init();
	
}
