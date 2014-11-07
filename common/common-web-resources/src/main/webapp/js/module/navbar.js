/**
 * Angular module :: ngcNavbar
 * 
 * The Angular module ngcNavbar provides functionality for the navbar.
 */
(function(angular) {
	
	'use strict';

	// Module
	
	var ngcNavbar = angular.module('ngcNavbar', []);

	// Directives
	
	/**
	 * Directive to set a navbar with a template.
	 * Add the ngc-navbar attribute to the element and set as value the html template with
	 * the navbar.
	 * Set the attribute ngc-navbar-active-item with the value of the active item. You need
	 * to add some AngularJS code to the html template to make the item active, for instance
	 * to add the active class.
	 */
	ngcNavbar.directive('ngcNavbar', function (templateProvider, applicationContext) {
		var defaultTemplateUrl = applicationContext.contextPath + '/console/template/navbar.html';
	    return {
			restrict: 'A',
			scope: {
	    		ngcNavbar: '@',
				ngcNavbarActiveItem: '@'
	    	},
	    	link:function (scope, element, attrs, ngModel) {
	        	var templateUrl = scope.ngcNavbar;
	        	if (!templateUrl || templateUrl == '') {
	        		templateUrl = defaultTemplateUrl;
	        	}
	        	templateProvider.compile(templateUrl, element, scope);
	        }
	    };
	});
	
	/**
	 * Directive to set a side navbar with a template.
	 * Add the ngc-side-nav attribute to the element and set as value the html template with
	 * the side navbar.
	 */
	ngcNavbar.directive('ngcSideNav', function (templateProvider) {
	    return {
			restrict: 'A',
			scope: {
	    		ngcSideNav: '@'
	    	},
	    	link:function (scope, element, attrs, ngModel) {
	        	var templateUrl = scope.ngcSideNav;
	        	if (!templateUrl || templateUrl == '') {
	        		alert('Missing value for ngc-side-nav, it should contain the link to a tempalte');
	        	} else {
	        		templateProvider.compile(templateUrl, element, scope);
	        	}
	        }
	    };
	});
	
	/**
	 * Directive to render the element as active by matching the href to the 
	 * $location.path, $location.absUrl or a list of alternative urls.
	 * When this attribute is set on a hyperlink, the url path in the href of the
	 * element is matched against the beginning of $location.path(the current url path). 
	 * If $location.path is empty, the $location.absPath(), without the protocol, 
	 * hostname and port, is used as current url path. The matching is done to the 
	 * beginning of the current url because it can contain some extra information 
	 * like state. If it is a match the value of the attribute is set as class, 
	 * otherwise it is removed as class. If the attribuate has not a value, the 
	 * class 'active' is used. No attribute value is required when using Bootstrap, 
	 * because Bootstrap uses the 'active' class.
	 * The attribute ngc-active-link-alternatives can be added to provide a 
	 * comma seperated list of url's which also activates this element.
	 */
	ngcNavbar.directive('ngcActiveLink', ['$location', function(location) {
		return {
			restrict: 'A',
			scope: {
	    		ngcActiveLink: '@',
	    		ngcActiveLinkAlternatives: '@'
	    	},
			link: function(scope, element, attrs, controller) {
	    		var activeLinkClass = 'active';
	    		var activeLinkAlternatives = null;
				if (attrs['ngcActiveLink'] != null && attrs['ngcActiveLink'] != '') {
	    			activeLinkClass = scope.ngcActiveLink;
	    		}
				if (attrs['ngcActiveLinkAlternatives'] != null && attrs['ngcActiveLinkAlternatives'] != '') {
					var a = scope.ngcActiveLinkAlternatives.replace(' ','');
					activeLinkAlternatives = a.split(',');
	    		}
				var path = attrs.href;
				scope.location = location;
				scope.$watch('location.path()', function(locationPath) {
					var newPath = locationPath;
					if (newPath == null || newPath == '') {
						newPath = location.absUrl();
						newPath = newPath.substr(newPath.indexOf('://')+3);
						newPath = newPath.substr(newPath.indexOf('/'));
						if (newPath.indexOf('?') != -1) {
							newPath = newPath.split('?')[0];
						}
					}
					if (newPath != null) {
						var matched = false;
						if (path === newPath.slice(0, path.length)) {
							matched = true;
						} else if (activeLinkAlternatives != null) {
							$.each(activeLinkAlternatives, function(index, item) {
								if (item === newPath.slice(0, path.length)) {
									matched = true;
								}							
							});
						}
						if (matched) {
							element.addClass(activeLinkClass);
						} else {
							element.removeClass(activeLinkClass);
						}
					}
				});
			}
		};
	}]);
	
})(window.angular);

function NavbarController($scope, $cookies) {
	
	// raw check, just for rendering purpose
	var isAuthenticated = ($cookies['cca'] !== undefined && $cookies['cca'] != null) ? true : false;

	$scope.isAuthenticated = function() {
		return isAuthenticated;
	}
	
}
