application.service('dynamicControlTemplateSelector', DynamicControlTemplateSelector);
application.service('dynamicControlTemplateLoaderService', DynamicControlTemplateLoaderService);

/**
 * By default the templates for the dynamic controls are loaded from: 
 *  ../js/module/metalisx-dynamic-control.tpl
 * This is not allways the right URL. So after the application is created you
 * can override this default by setting the $rootScope.metalisxDynamicControlTemplateUrl.
 * For example with:
 *   application.run(function($rootScope) {
 *     $rootScope.metalisxDynamicControlTemplateUrl = 'yourUrl';
 *   });
 */
application.run(function($rootScope) {

	if (!$rootScope.metalisxDynamicControlTemplateUrl) {
		$rootScope.metalisxDynamicControlTemplateUrl = '../js/module/metalisx-dynamic-control.tpl';
	}
	
});

/**
 * When using the dynamicControl in HTML it requires two attributes: 
 * - ngcEntity 
 * - ngcField
 * 
 * Also a set of templates needs to be registered in the $templateCache for
 * rendering HTML objects.The templates can use the values in the scope objects:
 *  - ngcEntity
 *  - ngcField
 * 
 * ngcEntity contains: 
 *  - item which contains all values 
 *  - metatdata which contains fields which contains a list of all meta data 
 *    describing all fields in the object
 * 
 * ngcField contains the active field(member of the fields list in ngcEntity)
 * 
 * The complete entity is passed to the template so all information of the
 * object is accessible if the active field depends on metadata or values of
 * other fields.
 * 
 * The templates needs to be reqisterd in $templateCache before this directive
 * can be used. Templates with the following url needs to be present: 
 *  - dynamic-control-panel.html for adding save and cancel buttons to a group
 *  - dynamic-control-group.html for combining controls
 *  - dynamic-control-date-input.html for a HTML input with a date value
 *  - dynamic-control-input.html for a HTML input with a string value or a not registered types
 *  
 * Registering templates with the script tag and type text/ng-template is the
 * easiest way. Unfortunately these script tags should be in the HTML code and
 * as such located in the HTML block with the ng-app attribute.
 */
application.directive('dynamicControl', function(dynamicControlTemplateSelector, templateCompile) {
	return {
		restrict: 'E',
		scope: {
			ngcEntity: '=',
			ngcField: '='
		},
		transclude: true,
        link:function (scope, element, attrs) {
        	var html = dynamicControlTemplateSelector.getTemplate(scope.ngcField.type);
        	templateCompile.compile(html, element, scope);
        }
    };
});

application.directive('dynamicControlGroup', function(dynamicControlTemplateSelector, templateCompile) {
	return {
		restrict: 'E',
		scope: {
			ngcEntity: '=',
		},
		transclude: true,
        link:function (scope, element, attrs) {
        	var html = dynamicControlTemplateSelector.getTemplate("group");
        	templateCompile.compile(html, element, scope);
        }
    };
});

application.directive('dynamicControlPanel', function(dynamicControlTemplateSelector, templateCompile) {
	return {
		restrict: 'E',
		scope: {
			ngcId: '@',
			ngcEntity: '=',
			ngcSave: '=',
			ngcCancel: '='
		},
		transclude: true,
        link:function (scope, element, attrs) {
        	var html = dynamicControlTemplateSelector.getTemplate("panel");
        	templateCompile.compile(html, element, scope);
        }
    };
});

function DynamicControlTemplateLoaderService($rootScope, $injector) {
	
	this.loadTemplates = function() {
		/**
		 * The jQuery ajax method in synchronous mode is used because
		 * we need to be sure the templates are loaded before the
		 * directives are used.
		 */ 
		$.ajax({
		    url: $rootScope.metalisxDynamicControlTemplateUrl,
		    success: function(html) {
				$injector.get("$compile")(html);
		    },
		    async:false
		  });
	};
	
}

function DynamicControlTemplateSelector($templateCache, dynamicControlTemplateLoaderService) {

	// Crude check if the templates are present in the $templateCache.
	if (!$templateCache.get('dynamic-control-input.html')) {
		dynamicControlTemplateLoaderService.loadTemplates();
	}
	
	this.getTemplate = function(type) {
		var html = '';
    	if (type == 'date') {
    		html = $templateCache.get('dynamic-control-date-input.html');
    	} else if (type == 'group') {
    		html = $templateCache.get('dynamic-control-group.html');
    	} else if (type == 'panel') {
    		html = $templateCache.get('dynamic-control-panel.html');
    	} else {
    		html = $templateCache.get('dynamic-control-input.html');
    	}
    	return html;
	};
	
};
