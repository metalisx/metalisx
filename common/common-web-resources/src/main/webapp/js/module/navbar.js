application.directive('ngcNavbar', function (templateProvider) {
	var templateUrl = '../template/navbar.html';
    return {
		restrict: 'A',
		scope: {
    		ngcNavbar: '@',
			ngcNavbarTemplate: '@'
    	},
    	link:function (scope, element, attrs, ngModel) {
    		if (!scope.ngcNavbarTemplate) {
    			scope.ngcNavbarTemplate = templateUrl;
    		}
        	scope.navbarActiveItem = scope.ngcNavbar || '';
        	templateProvider.compile(scope.ngcNavbarTemplate, element, scope);
        }
    };
});

application.directive('ngcSideNav', function (templateProvider) {
    return {
		restrict: 'A',
		scope: {
    		ngcSideNav: '@',
			ngcSideNavTemplate: '@'
    	},
    	link:function (scope, element, attrs, ngModel) {
    		if (!scope.ngcSideNavTemplate) {
    			alert('Missing ngc-side-nav-template attribute');
    		}
        	scope.sideNavActiveItem = scope.ngcSideNav || '';
        	templateProvider.compile(scope.ngcSideNavTemplate, element, scope);
        }
    };
});
