var application = angular.module('application', []);

application.service('messagesProvider', MessagesProvider);

application.service('templateService', TemplateService);

application.service('templateProvider', TemplateProvider);

application.service('templateCompile', TemplateCompile);

application.service('crudService', CrudService);

function MessagesProvider() {
	
	this.message = function(message, options) {
		$.metalisxMessages(message, options);
	};
	
	this.messages = function(messages, options) {
		$.metalisxMessages(messages, options);
	};
	
}

function TemplateService($http) {
	var time = '?time=' + (new Date()).getTime();
	this.getTemplate = function(templateUrl, onsuccess) {
		// For some reason the $http sometimes failed to execute the Ajax call 
		// so switched to the $.metalisxDataProvider.
		$.metalisxDataProvider.get(templateUrl + time, null, {
			contentType: null,
			dataType: 'html',
			onsuccess: onsuccess
		});		
	};
}

function TemplateCompile($compile, $rootScope) {
	this.compile = function(html, element, scope) {
		element.html(
				$compile($.trim(html))(scope)
			);
		var phase = $rootScope.$$phase;
		if (phase != '$apply' && phase != '$digest') {
			scope.$digest();
		}
	};
}

function TemplateProvider(templateService, templateCompile) {
	this.compile = function(templateUrl, element, scope, onsuccess) {
		templateService.getTemplate(templateUrl, function(html) {
			templateCompile.compile(html, element, scope);
			if (onsuccess) {
				onsuccess();
			}
		});
	};
}

/*
 * To make rest calls uniform across applications a pattern
 * is developed. The re-occurring rest calls are combined 
 * in this AngularJS service.
 * 
 * A base url is provided as parameter for the method and
 * depending on the call the url path is extended according
 * to the Json object requested from the server.
 * 
 * Protocol - Path - Information
 *  GET  - /metatdata     - returns metadata JSON object of an entity
 *  POST - /page/metadata - returns metadata JSON object for a page containing entities
 *  POST - /inital/data   - returns a JSON object containing all information
 *                          needed to display an entity.
 *                          Useful when retrieving an entity, and showing
 *                          a property of the entity as a listbox.
 *                          To display the options in the listbox, we can use this
 *                          call to retrieve these list. Multiple if required.
 *                          This is done to prevent the entity to clutter with 
 *                          secondary information.
 *  POST - /page          - returns a JSON object containing a page with entities
 *  GET  - /new-entity    - returns a new entity JSON object of a server side entity.
 *                          This way the entity can be build on the server, so no
 *                          hacks or weird JavaScript is required.
 *  POST - /list          - returns a JSON object containing a list of entities
 *  GET  - /              - returns an entity
 *  POST - /              - update an entity
 *  PUT  - /              - insert an entity 
 *  DEL  - /              - deletes an entity
 *  
 *  The getPageEndpoint is a helper function to build the correct
 *  rest url for retrieving a page. This was required to include the jQuery
 *  DataTable in this pattern.
 *  
 *  The server side needs to implement the code handling the protocol and
 *  path. To use this service does not mean all needs to be implemented, only
 *  implement the one you need.
 */
function CrudService() {

	var restMetadataEndpoint = '/metadata';
	var restPageMetadataEndpoint = '/page/metadata';
	var restPageEndpoint = '/page';
	var restNewEntityEndpoint = '/new-entity';
	var restListEndpoint = '/list';
	var restInitialDataEndpoint = '/initial/data';

	function dataToUrlPath(data) {
		var value = '';
		if (data != null) {
			for(var property in data){
			    value = value + '/' + data[property];
			}
		}
		return value;
	}

	/* Returns the rest page endpoint, so it can be used by jQuery DataTable. */ 
	this.getPageEndpoint = function(url) {
		return url + restPageEndpoint;
	};
	
	this.metadata = function(url,  options) {
		$.metalisxDataProvider.get(url + restMetadataEndpoint, null, options);
	};

	this.pageMetadata = function(url, options) {
		$.metalisxDataProvider.get(url + restPageMetadataEndpoint, null, options);
	};
	
	this.getInitialData = function(url, data, options) {
		$.metalisxDataProvider.post(url + restInitialDataEndpoint, data, options);
	};
	
	this.getNewEntity = function(url, options) {
		$.metalisxDataProvider.get(url + restNewEntityEndpoint, null, options);
	};
	
	this.getList = function(url, data, options) {
		$.metalisxDataProvider.post(url + restListEndpoint, data, options);
	};
	
	this.get = function(url, data, options) {
		$.metalisxDataProvider.get(url + dataToUrlPath(data), null, options);
	};
	
	this.del = function(url, data, options) {
		$.metalisxDataProvider.del(url + dataToUrlPath(data), null, options);
	};
	
	this.post = function(url, data, options) {
		$.metalisxDataProvider.post(url, data, options);
	};
	
	this.put = function(url, data, options) {
		$.metalisxDataProvider.put(url, data, options);
	};

}

application.directive('ngcDateMilliseconds', function () {
    return {
		restrict: 'A',
        require:'ngModel',
        link:function (scope, element, attrs, ngModel) {

        	var dateFormat = 'dd-mm-yy';
       		var timeFormat = 'hh:mm:ss.l';

			function getFullDisplayDate(value) {
				if (!value) return null;
				var v = null;
				if (value.match(/^[0-3]\d-[01]\d-\d{4} [0-2]\d:[0-5]\d:[0-5]\d\.\d{3}$/) != null) { // date + hours + minutes + seconds + milliseconds, no timezone stuff
					v = value;
				} else if (value.match(/^[0-3]\d-[01]\d-\d{4} [0-2]\d:[0-5]\d:[0-5]\d$/) != null) { // without milliseconds
					v = value + '.000';
				} else if (value.match(/^[0-3]\d-[01]\d-\d{4} [0-2]\d:[0-5]\d$/) != null) { // without seconds
					v = value + ':00.000';
				} else if (value.match(/^[0-3]\d-[01]\d-\d{4} [0-2]\d$/) != null) { // without minutes
					v = value + ':00:00.000';
				} else if (value.match(/^[0-3]\d-[01]\d-\d{4}$/) != null) { // without hours
					v = value + ' 00:00:00.000';
				}
				return v;
			}
			
			function getFullIsoDateAsString(value) {
				if (!value) return null;
				var v = null;
				if (value.match(/^\d{4}-[01]\d-[0-3]\dT[0-2]\d:[0-5]\d:[0-5]\d\.\d{3}$/) != null) { // date + hours + minutes + seconds + milliseconds, no timezone stuff
					v = value;
				} else if (value.match(/^\d{4}-[01]\d-[0-3]\dT[0-2]\d:[0-5]\d:[0-5]\d$/) != null) { // without milliseconds
					v = value + '.000';
				} else if (value.match(/^\d{4}-[01]\d-[0-3]\dT[0-2]\d:[0-5]\d$/) != null) { // without seconds
					v = value + ':00.000';
				} else if (value.match(/^\d{4}-[01]\d-[0-3]\dT[0-2]\d$/) != null) { // without minutes
					v = value + ':00:00.000';
				} else if (value.match(/^\d{4}-[01]\d-[0-3]\d$/) != null) { // without hours
					v = value + 'T00:00:00.000';
				}
				return v;
			}

			ngModel.$parsers.unshift(function (viewValue) {
				var value = getFullDisplayDate(viewValue);
				if (value != null) {
					value = $.metalisxUtils.displayDateToIsoDateAsString(value);
				} else {
					ngModel.$setValidity('Incorrect date format.', false);
				}
				return value;
            });

			ngModel.$formatters.unshift(function (modelValue) {
				var value = getFullIsoDateAsString(modelValue);
				if (value != null && typeof value != 'undefined') {
					value = $.metalisxUtils.isoDateAsStringtoDisplayDate(value);
				}
				return value;
            });

			element.datetimepicker({
    			dateFormat: dateFormat,
    			showButtonPanel: true,
    			changeMonth: true,
    			changeYear: true,
    			showSecond: true,
    			showMillisec: true,
    			timeFormat: timeFormat,
    			onSelect: function(dateText, inst) {
    				ngModel.$setViewValue(element.val());
    				scope.$apply();
     			}    
            });
        }
    };
});

/**
 * Directive to activate a HTML object's click event when the enter key is
 * activated in the parent HTML object.
 * Specify the ngc-enter attribute on the parent HTML object.
 * Use as value a selector to identify the HTML object on which the click
 * event needs to be activated when the enter key is pressed. The value
 * should point to a child HTML object.
 * 
 * The focus is moved from the current focused element to this element, this
 * to activate the blur event on the current focused element. After the 
 * execution of the click event, the focus will be restored.
 *  
 * For example:
 *  <div ngc-enter="#saveButton">
 *    <a href="#" id="saveButton">Save</input>
 *  </div>
 * or:
 *  <div ngc-enter=".ngc-save">
 *    <a href="#" class="ngc-save">Save</input>
 *  </div>
 */
application.directive('ngcEnter', function () {
    return {
		restrict: 'A',
        link:function (scope, element, attrs) {
        	var ngcEnter = attrs['ngcEnter'];
        	if (!ngcEnter) {
        		throw new Error('The ngc-enter attribute needs to contain a selector value which resolves to a child HTML object.');
        	}
        	var $enterElement = $(ngcEnter, element);
        	if ($enterElement.size() < 1) {
        		throw new Error('The selector value in ngc-enter attribute can not be resolved to a child HTML object.');
        	}
        	if ($enterElement.size() > 1) {
        		throw new Error('The selector value should resolve to only one child HTML object.');
        	}
    		element.keypress(function(e) {
    			if ((e.which ? e.which : e.keyCode) == 13) {
    				e.stopPropagation();
    				var currentElement = $(":focus");
    				$enterElement.focus();
    				$enterElement.click();
    				if (currentElement.size() > 0) {
    					currentElement.get(0).focus();
    				}
    			}
    		});
        }
    };
});

application.directive('ngcFocus', function () {
    return {
		restrict: 'A',
        link:function (scope, element, attrs) {
       		element.focus();
        }
    };
});

/**
 * Directive for enabling Twitter typeahead on an HTML input field.
 * The required HTML input attributes are:
 *  - ngc-typeahead
 *  - ngc-typeahead-model
 * The optional HTML input attributes are: 
 *  - ngc-typeahead-value-key
 * ngc-typeahead Constains the REST endpoint. The endponit should contain the 
 * string %QUERY, this will be replaced with the value entered in the input field. 
 * The REST endpoint should return a list with in every object a value.
 * ngc-typeahead-model The object in which the input field value is placed when 
 *   the blur event executes.
 * ngc-typeahead-value-key Is the fieldname in the objects of the list returned by the
 *   REST call containing the value. This value is visible in the suggestions list and
 *   is placed in the input field when a suggestion is selected. If not set the 
 *   fieldname defaults to value.
 */
application.directive('ngcTypeahead', function () {
    return {
		restrict: 'A',
		scope: {
			ngcTypeahead: '@',
    		ngcTypeaheadModel: '=',
			ngcTypeaheadValueKey: '@'
		},
		transclude: true,
		link:function (scope, element, attrs) {

			var idKey = null;
			var valueKey = null;
			
			if (scope.ngcTypeahead == null || scope.ngcTypeahead == '') {
				alert('The ngc-typeahead attribute is missing the value of the REST endpoint.');
			}
			if (!scope.ngcTypeaheadValueKey) {
				valueKey = 'value';
			} else {
				valueKey = scope.ngcTypeaheadValueKey;
			}

			function updateModel(value) {
				scope.ngcTypeaheadModel = value;
				scope.$apply();
			}
			
			var $typeahead = null;
			function createTypeahead() {
				$typeahead = element.typeahead('destroy').typeahead({
					remote: scope.ngcTypeahead,
					cache: false,
					valueKey: valueKey,
				});
			}
			
			element.on('blur', function(event) {
				updateModel($(this).val());
			});
			
			scope.$watch('ngcTypeaheadModel', function(newValue, oldValue) {
				if (newValue == null || newValue == '') {
					$typeahead.typeahead('setQuery', '');
				}
			});
			
			createTypeahead();
        }
    };
});
