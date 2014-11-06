var application = angular.module('application', ['ngRoute', 'ngCookies', 'ngcMessages']);

application.factory('applicationCache', ApplicationCache);

application.service('applicationContext', ApplicationContext);

application.service('messagesProvider', MessagesProvider);

application.service('templateService', TemplateService);

application.service('templateProvider', TemplateProvider);

application.service('templateCompile', TemplateCompile);

application.service('crudService', CrudService);

application.service('browserUrlService', BrowserUrlService);

application.service('utilsService', UtilsService);

application.run(function($rootScope, applicationContext) {
    $rootScope.applicationContext = applicationContext;
})

/* 
 * Factory providing a central point to cache application data,
 * so this information is injectable through out the application.
 * 
 * The purpose for this cache factory is to limit the number 
 * of REST calls to the back-end for data which does not change 
 * much.
 */
function ApplicationCache($cacheFactory) {

	var cacheName = 'applicationCache';
	
	return $cacheFactory(cacheName);

}

/* 
 * Service providing the information for an application context,
 * so this information is injectable through out the application.
 * 
 * Extend it with information you require.
 */
function ApplicationContext($cacheFactory, $locale) {

	/**
	 * Locale is hardcoded set to euro, overruling the
	 * locale in the browser.
	 */
	$locale.NUMBER_FORMATS.CURRENCY_SYM = '&euro; ';
	$locale.NUMBER_FORMATS.DECIMAL_SEP = ',';
	$locale.NUMBER_FORMATS.GROUP_SEP = '';
	
	/**
	 * Set it to store a prefix for all urls at a single place.
	 * This is only a definition and is not automatically used 
	 * by the code, filters and directives. 
	 */
	this.contextPath = '';

	/**
	 * Definition for the model type of a date at a single place.
	 * Valid values are string and number
	 * If the value is string then the format of a date in 
	 * the model is yyyy-MM-ddTHH:mm:ss.SSS.
	 * If the value is number then the value is the number of 
	 * milliseconds since 1970/01/01.
	 * 
	 * This is only a definition and is not automatically used 
	 * by the code, filters and directives. Use it to set the 
	 * parameter type in the filter ngcDateModel so switching 
	 * the type is easy from this point.
	 * 
	 * The REST endpoint on the server should return the dates in 
	 * the specified type and the client should send dates in 
	 * the specified type.
	 */
	this.dateModelType = 'string';
	
}

function BrowserUrlService($rootScope, $location) {
	
	/*
	 *  Replace the browsers url without the page reloading.
	 */
	this.replaceUrl = function(url) {
		
		function updateLocation(url){
			$location.path(url);
			history.replaceState({}, url, url);
		}

		var off = $rootScope.$on('$routeChangeStart', function(event){ 
		    event.preventDefault();
		    off(); // remove the listener to prevent url changes 
		});

		updateLocation(url);
		
	}
	
}

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
		element.html($.trim(html));
		$compile(element.contents())(scope);
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
 *  GET  - /filter        - returns a new filter DTO JSON object of a server side filter DTO.
 *                          This way the filter object for filtering a page or list can be
 *                          build on the server, so no hack or weird JavaScript is required.
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
	var restFilterEndpoint = '/filter';
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
	
	this.getFilter = function(url, options) {
		$.metalisxDataProvider.get(url + restFilterEndpoint, null, options);
	};
	
	this.getNewEntity = function(url, options) {
		$.metalisxDataProvider.get(url + restNewEntityEndpoint, null, options);
	};
	
	this.postNewEntity = function(url, data, options) {
		$.metalisxDataProvider.post(url + restNewEntityEndpoint, data, options);
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

/**
 * Utils service.
 * For defining some helper functions so we do not need jQuery in
 * the controllers.
 */
function UtilsService($rootScope) {
	
	/**
	 * Method to replace booelan 0 and 1 into respective No and Yes.
	 */
	this.booleanIntegerToString = function(booleanAsInteger) {
		return booleanAsInteger == 1 ? 'Yes' : 'No';
	}
	
	/**
	 * Method to check if the url parameter is empty.
	 * The parameter should be the value of the parameter.
	 * It returns true if the value is:
	 *  - undefined = the value is not present because the parameter was not present
	 *  - '' = an empty string
	 *  - 'null' = a parameter value of null will be converted to string 'null'
	 */
	this.isUrlParamEmpty = function($value) {
		return $value === undefined || $value === '' || $value === 'null';
	}

	/**
	 * Method returns true if an apply or digest is in progress
	 * otherwise it return false.
	 */
	this.isApplyInProgress = function() {
		var inProgress = false;
		var phase = $rootScope.$$phase;
		if (phase === '$apply' || phase === '$digest') {
			inProgress = true;
		}
		return inProgress;
	};

}

/**
 * Requires the jQuery UI datetpicker plugin. And requires the jQuery Timepicker Addon
 * for adding a timepicker to the jQuery UI datepicker.
 * 
 * The following attribute can be set on the element: 
 *  - ngc-datepicker-show-timepicker 
 *  - ngc-datepicker-model-type
 *  
 * ngc-datepicker-show-timepicker
 * Allowed values are true and false. When true a timepicker is shown when false not.
 * When false the time is stripped from the view value and when the model value is 
 * updated the time is set to all zeros.
 * 
 * ngc-datepicker-model-type
 * Indicator how to handle the model value as a string or as a number.
 * Allowed values are string and number. The value string is the default.
 * 
 * The date format in de view when ngc-datepicker-show-timepicker is true is: 
 * dd-mm-yy hh:mm:ss.l. And when false it is: dd-mm-yy.
 * 
 * The date format in the model when ngc-datepicker-model-type is string 
 * is: yyyy-MM-ddTHH:mm:ss.SSS. And when number it is a number representation of
 * the date.
 * 
 * We are not using isolated scope to prevent, when multiple directives are set, the error: 
 *  Multiple directives [..] asking for new/isolated scope
 */
application.directive('ngcDatepicker', function ($timeout, $filter) {
    return {
		restrict: 'A',
        require:'ngModel',
        link:function (scope, element, attrs, ngModel) {

        	var showTimepicker = false;
        	var dateFormat = 'dd-mm-yy';
       		var timeFormat = 'hh:mm:ss.l';
       		var modelType = 'string';
       		
			function viewDateToModelDate(value) {
				var v = value + (showTimepicker === false ? ' 00:00:00.000' : '');
				return $filter('ngcDateModel')(v, modelType);
			}

			function modelDateToViewDate(value) {
				return $filter('ngcDate')(value);
			}

			if (attrs['ngcDatepickerShowTimepicker']) {
        		if (typeof attrs.ngcDatepickerShowTimepicker === 'boolean') {
            		showTimepicker = attrs.ngcDatepickerShowTimepicker;
        		} else if (typeof attrs.ngcDatepickerShowTimepicker === 'string') {
            		showTimepicker = attrs.ngcDatepickerShowTimepicker === 'true' ? true : false; 
        		}
        	}

			if (attrs['ngcDatepickerModelType']) {
				modelType = attrs.ngcDatepickerModelType;
				if (modelType != 'string' && modelType != 'number') {
					alert('Invalid value ' + modelType + ' for attribute ngc-datepicker-model-type. Allowed values are string and number');
				}
			}
			
			// View value to model value
			ngModel.$parsers.unshift(function (viewValue) {
				var value = null;
				if (viewValue != null) {
					value = viewDateToModelDate(viewValue);
				} else {
					ngModel.$setValidity('Incorrect date format.', false);
				}
				return value;
            });

	    	// Model value to view value
			ngModel.$formatters.unshift(function (modelValue) {
				var value = null;
				if (modelValue != null && typeof modelValue != 'undefined') {
					value = modelDateToViewDate(modelValue);
				}
				return value;
            });

			// $timeout is used otherwise the following error is thrown: 
			//  Uncaught Missing instance data for this datepicker
			$timeout(function() {
				element.datetimepicker({
	    			dateFormat: dateFormat,
	    			showButtonPanel: true,
	    			changeMonth: true,
	    			changeYear: true,
	    			showTimepicker: showTimepicker,
	    			showSecond: true,
	    			showMillisec: true,
	    			timeFormat: timeFormat,
	    			onSelect: function(dateText, inst) {
	    				ngModel.$setViewValue(element.val());
	    				scope.$apply();
	     			}    
	            });
				
				// Destroy the datatable when the element is removed from the DOM.
				// This is required when the ng-view is used.
				element.bind("$destroy", function() {
					element.datepicker("destroy");
		        });
			});
			
        }
    };
});

/**
 * Directive to activate a HTML object's click event when the enter key is
 * activated in the parent HTML object.
 * Specify the ngc-enter attribute on the parent HTML object.
 * The value should contain a selector to identify the HTML object on which the click
 * event needs to be activated when the enter key is pressed. The value
 * should point to a child HTML object. If the child HTML object can not be found
 * nothing will happen if the enter key is pressed.
 * 
 * The parent HTML object and the HTML object should be visible otherwise the 
 * click event will not happen.
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
        		throw new Error('The ngc-enter attribute should contain a selector value which resolves to a child HTML object.');
        	}
        	var $enterElement = $(ngcEnter, element);
        	if ($enterElement.size() > 1) {
        		throw new Error('The selector value in ngc-enter attribute should resolve to only one child HTML object.');
        	}
        	if ($enterElement.size() == 1) {
	    		element.keypress(function(e) {
	    			if (!element.hasClass('ng-hide') && $(element).is(":visible") &&
	    				!$($enterElement).hasClass('ng-hide') && $($enterElement).is(":visible")) {
	    				var nodeName = null;
						if (e && e.target && e.target.nodeName) {
							nodeName = e.target.nodeName.toUpperCase();
						}
						if ((e.which ? e.which : e.keyCode) == 13 && nodeName != 'TEXTAREA') {
							e.stopPropagation();
							var currentElement = $(":focus");
							$enterElement.focus();
							$enterElement.click();
							if (currentElement.size() > 0) {
								currentElement.get(0).focus();
							}
						}
	    			}
	    		});
        	}
        }
    };
});

/**
 * Directive to set focus on the element the attribute is set on.
 * 
 * The optional HTML input attributes are: 
 *  - ngc-focus-enabled
 * 
 * ngc-focus-enabled You can set this attribute on one element to true,
 * if the attribute is set on multiple elements.
 */
application.directive('ngcFocus', function ($timeout) {
    return {
		restrict: 'A',
        link:function (scope, element, attrs) {
    		var ngcFocusEnabled = true;
        	if (attrs['ngcFocusEnabled']) {
        		if (typeof attrs.ngcFocusEnabled === 'boolean') {
        			ngcFocusEnabled = attrs.ngcFocusEnabled;
        		} else if (typeof attrs.ngcFocusEnabled === 'string') {
            		ngcFocusEnabled = attrs.ngcFocusEnabled === 'true' ? true : false; 
        		}
        	}
    		// The timeout is used to take the ng-show in account.
    		$timeout(function() {
    			if (ngcFocusEnabled === true) {
// 					Parent check removed    				
//  	   				element.parents('.ng-hide').length == 0 && element.parents(':hidden').length == 0
					if (!element.hasClass('ng-hide') && element.is(':visible') ) {
	        			element.focus();
	        		}
    			}
    		});
        }
    };
});

application.directive('ngcStopPropagation', function () {
    return {
		restrict: 'A',
        link:function (scope, element, attrs) {
        	element.click(function(event) {
				event.stopPropagation();
			});
        }
    };
});


/**
 * Directive for enabling Twitter typeahead on an HTML input field.
 * Requires the javascripts files for Twitter typeahead and bloodhound 
 * version 0.10.2+.
 * The required HTML input attributes are:
 *  - ngc-typeahead
 *  - ngc-typeahead-model
 * The optional HTML input attributes are: 
 *  - ngc-typeahead-value-key
 *  - ngc-typeahead-callback
 * ngc-typeahead Constains the REST endpoint. The endponit should contain the 
 * string %QUERY, this will be replaced with the value entered in the input field.
 * The endpoint can also use the %LIMIT, which will be replaced with the number
 * of items in by the popup menu. 
 * The REST endpoint should return a list with in every object a value.
 * ngc-typeahead-model The object in which the input field value is placed when 
 *   the blur event executes.
 * ngc-typeahead-value-key Is the fieldname in the objects of the list returned by the
 *   REST call containing the value. This value is visible in the suggestions list and
 *   is placed in the input field when a suggestion is selected. If not set the 
 *   fieldname defaults to value.
 * ngc-typeahead-callback Is the function which is called when the event 
 *   'typeahead:selected' is triggerd. The directive passes the data of the item
 *   to the method. The value of the attribute should be like: myFunction(typeahead, datum)
 * 
 * When the blur event is triggerd the model is updated and the typeahead field is 
 * cleared. Clearing the field is done to make the pluging work consistent. You can
 * use the ngc-typeahead-callback to do something with the selected suggestion before 
 * the blur event clears it.
 */
application.directive('ngcTypeahead', function () {
    return {
		restrict: 'A',
		scope: {
			ngcTypeahead: '@',
    		ngcTypeaheadModel: '=',
			ngcTypeaheadValueKey: '@',
			ngcTypeaheadCallback: '&'
		},
		transclude: true,
		link:function (scope, element, attrs) {

			var url = null;
			var callback = null;
			var idKey = null;
			var valueKey = null;
			var limit = 20;
			
			if (attrs['ngcTypeahead'] == null || attrs['ngcTypeahead'] == '') {
				alert('The ngc-typeahead attribute is missing the value of the REST endpoint.');
			} else {
				url = scope.ngcTypeahead;
			}
			if (attrs['ngcTypeaheadCallback'] && attrs['ngcTypeaheadCallback'] != '') {
				callback = scope.ngcTypeaheadCallback;
			}
			if (attrs['ngcTypeaheadValueKey'] == null || attrs['ngcTypeaheadValueKey'] == '') {
				valueKey = 'value';
			} else {
				valueKey = scope.ngcTypeaheadValueKey;
			}

			// Add the limit to the url so the remote can limit it's query
			url = url.replace('%LIMIT', limit);
			
			function updateModel(value) {
				scope.ngcTypeaheadModel = value;
				scope.$apply();
			}

			var engine = new Bloodhound({
				name: 'ngcTypeaheadBloodhound',
				limit: limit,
				minLength: 1,
				remote: url,
				datumTokenizer: function(d) { 
					return Bloodhound.tokenizers.whitespace(d.val); 
				},
				queryTokenizer: Bloodhound.tokenizers.whitespace
			});
			engine.initialize();

			var $typeahead = null;
			var datasetName = 'ngcTypeaheadDataset';
			function createTypeahead() {
				element.typeahead('destroy');
				$typeahead = element.typeahead(null, {
					name: datasetName,
					source: engine.ttAdapter(),
					displayKey: valueKey
				}).on('typeahead:selected', function(obj, datum, name) {
					if (callback) {
						callback({
							typeahead: $typeahead,
							datum: datum
						});
					}
				}).on('typeahead:closed', function() {
					if (callback) {
						callback({
							typeahead: $typeahead,
							datum: null
						});
					}
				});
				element.data('ttTypeahead').minLength = 0;
				/* 
				 * Twitter typeahead has the style vertical-align set to top. When using
				 * Bootstrap the input is not inline with elements on the same
				 * line. But because Twitter typeahead has the style directly set on the 
				 * element, we can not overrule it with css. Therefore the solution is
				 * hardcoded here.
				 */
				$typeahead.css('vertical-align', 'middle');
			}
			
			element.on('blur', function(event) {
				updateModel($typeahead.typeahead('val'));
			});
			
			scope.$watch('ngcTypeaheadModel', function(newValue, oldValue) {
				// Using $typeahead.typeahead('val', newValue); will open the dropdown menu
				var typeaheadInput= element.data('ttTypeahead').input;
				typeaheadInput.setQuery(newValue);
				typeaheadInput.resetInputValue();
			});
			
			createTypeahead();
        }
    };
});

/**
 * Directive to append a color picker to the element.
 * Add ngc-color-picker-settings attribut to overrule and/or
 * add settings to the default settings. Point it to a scope
 * object containing the color picker settings.
 * 
 * The color picker value is pushed to the ngModel when
 * the 'choose' button is clicked.
 * But if the showButtons settings is set to false the color
 * change is immediate pushed to the ngModel.
 * 
 * Requires Spectrum colorpicker javascriptfile spectrum.js.
 */
application.directive('ngcColorPicker', function () {
    return {
		restrict: 'A',
		require: 'ngModel', // filled after clicking the save button
		scope: {ngcColorPickerSettings:'='},
        link:function (scope, element, attrs, ngModel) {

			var settings = {
					preferredFormat: "hex",
					allowEmpty: true,
					showButtons: true,
					move: function(color) {
						if (!settings.showButtons) {
			            	scope.$apply(function () {
			            		ngModel.$setViewValue(color);
			            	});
						}
					},
					change: function(color) {
		            	scope.$apply(function () {
		            		ngModel.$setViewValue(color);
		            	});
					}
				};
			
			if (attrs['ngcColorPickerSettings'] != null && attrs['ngcColorPickerSettings'] != '') {
				settings = $.extend(settings, scope.ngcColorPickerSettings || {});
    		}
			element.spectrum(settings);
			
			// Instead of requiring an addition to the css, the input field color is set to white.
			$('.sp-input', element.parent()).css('background-color','#ffffff');

            scope.$watch('ngModel', function(newValue, oldValue) {
            	element.spectrum('set', ngModel.$viewValue || '');
			});

            if (!settings.showButtons) {
				$('.sp-input', element.parent()).on('keyup', function () {
					var val = $(this).val();
					if (val == '' || val.match(/^#([0-9a-f]{3}|[0-9a-f]{6})$/i) != null) {
		            	scope.$apply(function () {
		            		ngModel.$setViewValue(val);
		            	});
					}
	            });
            }

        }
    };
});

//(function($){
//	  $.event.special.destroyed = {
//	    remove: function(o) {
//	      if (o.handler) {
//	        o.handler()
//	      }
//	    }
//	  }
//})(jQuery)

/**
 * Directive for file upload with HTML 5 functionality.
 * 
 * If a file is selected the following actions are executed.
 * The filename is placed in the model property specified by 
 * ngc-file-upload-filename.
 * The mime type is placed in the model property specified by 
 * ngc-file-upload-mime-type.
 * The data is converted with readAsDataUrl to base64 and placed
 * in the property specified by ngc-file-upload-document.
 * 
 * The ngc-file-upload-file-limit is an optional attribute to limit the size
 * of the uploaded file. It is specified in bytes and default set to 5242880.
 * 
 * The model is updated with the base64 representation of the file.
 */
application.directive('ngcFileUpload', function ($timeout, messagesProvider) {
	
    return {
		restrict: 'A',
		scope: {ngcFileUploadDocument:'=',
				ngcFileUploadFilename:'=',
				ngcFileUploadMimeType:'=',
				ngcFileUploadFileLimit:'='},
        link:function (scope, element, attrs) {

        	if (!(window.File && window.FileReader)) {
        		console.log('Uploading files will not work because the File APIs are not supported in the browser.');
       		}
        	
        	var fileLimit = 5242880;
        	
			if (attrs['ngcFileUploadFileLimit']) {
    			fileLimit = scope.ngcFileUploadFileLimit;
			}
			
			function handleFileSelect(evt) {
        		var files = evt.target.files;
        		var file = files[0];
        		getData(file);
			}

        	function getData(file) {
        		var reader = new FileReader();
        		reader.onload = function(e) {
        			if (fileLimit != null && file.size > fileLimit) {
        				messagesProvider.message(
        						'Can not upload file. Maximum file size allowed is ' + fileLimit + ' bytes.',
        						{'level': 'error'});
        			} else {
	        			if (reader.readyState === FileReader.DONE) {
		        			if (attrs['ngcFileUploadDocument']) {
		            			scope.ngcFileUploadDocument = reader.result;
		        			}
		        			if (attrs['ngcFileUploadFilename']) {
		        				scope.ngcFileUploadFilename = file.name;
		        			}
		        			if (attrs['ngcFileUploadMimeType']) {
		        				scope.ngcFileUploadMimeType = file.type;
		        			}
		    				scope.$apply();
	        			} else {
	        				messagesProvider.message('File loading failed. ' + reader.error,
	        						{'level': 'error'});
	        			}
        			}
        		};
        		reader.readAsDataURL(file);
        	}

			element.on('change', function(evt) {
				handleFileSelect(evt);
			});
        	
        }
    };
});

/**
 * Directive to append an icon to the field. When clicked a popup window
 * with a filebrowser is opened. On selecting a filename in the popup the
 * filename is set in the field an the model.
 * Set the ngc-filebrowser-url attribute to override the default url, for
 * example: /filebrowser/filebrowser.html
 */
application.directive('ngcFilebrowser', function () {
	
    return {
		restrict: 'A',
		require: 'ngModel',
        link:function (scope, element, attrs, ngModel) {
    		var width = 640;
    		var height = 480;
			var left = (screen.width / 2) - (width / 2);
			var top = (screen.height / 2) - (height / 2);
    		var config = {
       			url: '/filebrowser/filebrowser.html',
    			windowName: 'Filebrowser',
    			windowFeatures: 'width=' + width + ',height=' + height + ',left=' + left + ',top=' + top,
    			searchIconStyle: 'position: absolute; right: 25px; top: 10px;'
    		}
    		if (attrs['ngcFilebrowserUrl'] && attrs['ngcFilebrowserUrl'] != '') {
				config.url = attrs['ngcFilebrowserUrl']; 
			}
    		config.url = config.url + '?caller=custom&callback=ngcFilebrowserCallback';
    		$filebrowserButton = $('<i class="glyphicon glyphicon-search"></i>');
    		$filebrowserButton.attr('style', config.searchIconStyle);
    		$filebrowserButton.click(function() {
        		window.ngcFilebrowserCallback = function(filename) {
        			element.val(filename);
					ngModel.$setViewValue(filename);
					scope.$apply();
        		}
                window.open(config.url, config.windowName, config.windowFeatures);
    		});
    		element.parent().append($filebrowserButton);
        }
    };
});
	
/**
 * Directive to enable CKEditor on a textarea element.
 * Requires CKEditor 4 javascript files.
 * Steps:
 *  - download the CKEditor Javascript files from: http://ckeditor.com/
 *  - place the files on your server
 *  - include the Javascript files in your page
 *  - add the directive as attribute ngc-ckeditor to a textarea element
 * The <CKEditor directory>/config.js is used by default you can override this
 * default by adding the ngc-ckeditor-config attribute to the textarea
 * containing a the custom config file name.
 * You can set the mode by setting the ngc-ckeditor-mode attribute on the
 * element. Valid values are source and wysiwyg.
 * You can set the method to use when the save button is clicked by setting
 * the ngc-ckeditor-save-method attribute on the element. If not set the directive
 * will look for the method editorSave in the parent scope and if found this
 * method is used otherwise nothing will happen when the save button is 
 * clicked. The save method does not take any parameters because the value 
 * is in the model.
 */
application.directive('ngcCkeditor', function () {
	
    return {
		restrict: 'A',
		require: 'ngModel',
		scope: {ngcCkeditorSaveMethod:'&'},
        link:function (scope, element, attrs, ngModel) {
    	
    		function addSourceModeEvent(editor) {
				// Adding key up event to track changes if the editor in source mode
    			$('textarea.cke_source', element.parent()).on('keyup', function() {
					ngModel.$setViewValue(editor.getData());
					// Used to have $apply here, but do not know why. 
					// Everything seems to work without it.
					// It is removed as it will trigger all watchers by every keyup.
					// scope.$apply();
					// scope.$digest();
				});
    		}
    	
    		if (typeof CKEDITOR === 'undefined') {
    			alert('Please include the CKEditor javascript files or remove the AngularJS directive from the element.');
    		} else {
    			// a custom event handler is create for the editor so we can listen
    			// for the remove event when the textarea of the editor is removed from the DOM.
    			if ($.event && $.event.special && !$.event.special.ngcCkeditorDestroy) {
	    			(function($){
	    				  $.event.special.ngcCkeditorDestroy = {
	    				    remove: function(o) {
	    				      if (o.handler) {
	    				        o.handler(this, arguments)
	    				      }
	    				    }
	    				  }
	    				})(jQuery);
    			}
    			var config = null;
    			if (attrs['ngcCkeditorConfig'] && attrs['ngcCkeditorConfig'] != '') {
    				config = $.extend(config || {}, {
    					customConfig: attrs['ngcCkeditorConfig']
    				});
    			}
    			var mode = 'wysiwyg';
    			if (attrs['ngcCkeditorMode'] && attrs['ngcCkeditorMode'] != '') {
    				mode = attrs['ngcCkeditorMode'];
    				if (mode != 'source' && mode != 'wysiwyg') {
    					alert('Unknown mode ' + mode);
    				} else {
    					config = $.extend(config || {}, {
    						startupMode: mode
    					});
    				}
    			}
    			var saveMethod = null;
    			if (attrs['ngcCkeditorSaveMethod'] && attrs['ngcCkeditorSaveMethod'] != '') {
    				saveMethod = scope.ngcCkeditorSaveMethod;
    			} else if (scope.$parent.editorSave !== undefined) {
    				saveMethod = scope.$parent.editorSave;
    			}
    			
	    		setTimeout(function(){
	    			
	    			element.ckeditor(config, function() {
	    				
				    	var editor = element.ckeditorGet();
				    	
	    				addSourceModeEvent(editor);
		    			
		    			// Adding change event to track changes if the editor in wysiwyg mode
						editor.on('change', function() {
							ngModel.$setViewValue(this.getData());
							scope.$apply();
		    			});
						
		    			// If changes are mode in the source mode they are not pushed to the model.
		    			// The solution of pushing the changes to the model is by detecting a change
		    			// in mode and then push the data to the model. This means that if 
		    			// you do not switch back to the wysiwyg mode your changes are not in de model.
		    			this.on('mode', function( e ) {
		    				if (editor.mode == 'source') {
		    					addSourceModeEvent();
		    				}
		    			});

		    			// event to destroy the editor if the textarea is removed from the DOM
				    	element.on('ngcCkeditorDestroy', function() {
	    					editor.destroy();
						});
				    	
				    	// Enable the save method if we have a save method
				    	if (saveMethod != null) {
							editor.commands.save.enable();
							editor.addCommand('save', {
					            modes: {wysiwyg:1, source:1},
					            exec: function(editor) {
					                if (editor.checkDirty() && saveMethod != null) {
				                		saveMethod();
				                		editor.resetDirty();
					                }
					            }
						    });
				    	} else {
				    		editor.commands.save.disable();
				    	}
				    	
	    			});
	    		});
	    		
    		}
        }
    };
});

/**
 * Directive for DataTables.
 * ngcDataTableEnabled Is true or false. When false the element is not promoted
 * to a DataTable. This way you can prevent the execution of an AJAX request to
 * retrieve data, for instance with parent-child. The property is watched and if
 * it is changed from false to true the element is promoted to a DataTable.
 * 
 * ngcDataTableSettings Is the property containing the DataTables settings.
 * ngcDataTableFilter Is the filter send with the AJAX request to retrieve data.
 * ngcDataTableCallback Is the scope method executed after creating the dataTable.
 *   Use it in an attribute like: ngc-data-table-callback="myMethod(dataTable)"
 * Requires jQuery datatables.js 1.9.2 javascript files.
 */
application.directive('ngcDataTable', function () {
	
    return {
		restrict: 'A',
		scope: {ngcDataTableEnabled:'=',
    			ngcDataTableSettings:'=',
    			ngcDataTableFilter: '=',
    			ngcDataTableCallback: '&'},
        link:function (scope, element, attrs) {
    		
        	var callback = null;
        	var dataTable = null;
        	
    		function initDataTable() {
    			metalisxDataTable = element.metalisxDataTable(filter, scope.ngcDataTableSettings);
    			dataTable = metalisxDataTable.getDataTable();
    			if (callback) {
    				callback({
    					dataTable: dataTable
    				})
    			}
    			// Destroy the datatable when the element is removed from the DOM.
    			// This is required when the ng-view is used.
    			element.bind("$destroy", function() {
    				// Destroying the table generates an infinite loop.
    				//dataTable.fnDestroy();
    	        });
    		}

    		function addWatcher() {
				scope.$watch("ngcDataTableEnabled", function() {
					if (scope.ngcDataTableEnabled === true) {
						if (dataTable == null) {
							initDataTable();
						} else {
							dataTable.dataTable().fnDraw();
						}
					}
			   });
    		}
    		
			var filter = null;
			
			if (attrs['ngcDataTableSettings'] == null || attrs['ngcDataTableSettings'] == '') {
				alert('The ngc-data-table-settings attribute is missing.');
			}
			if (attrs['ngcDataTableFilter'] != null && attrs['ngcDataTableFilter'] != '') {
				filter = scope.ngcDataTableFilter;
			}
			if (attrs['ngcDataTableCallback'] && attrs['ngcDataTableCallback'] != '') {
				callback = scope.ngcDataTableCallback;
			}

    		if ($.fn.dataTable === undefined) {
    			alert('Please include the DataTables javascript files or remove the AngularJS directive from the element.');
    		} else {
    			var enabled = true;
    			if (attrs['ngcDataTableEnabled'] && attrs['ngcDataTableEnabled'] != '' && 
    					(scope.ngcDataTableEnabled === false || scope.ngcDataTableEnabled === 'false')) {
    				enabled = false;
    				// We watch if it is changed from disabled to enabled, not the otherway arround.
    				// If it is changed to true the element is promoted to a DataTable.
    				addWatcher();
    			}
    			// If it is not enabeld we do nothing.
    			if (enabled) {
    				initDataTable();
    				addWatcher();
    			}
    		}
        }
    };
});


/**
 * Directive to copy the content of the model to the clipboard
 * with the javascirpt plugin ZeroClipboard version 2.0.0.
 * The attribute zeroClipboardSettings is an object for overriding
 * or providing ZeroClipboard settings.
 * The attribute zeroClipboardDataProvider should contain a method
 * returning the data to be copied to the clipboard. Defaults to
 * zeroClipboardDataProvider.
 */
application.directive('ngcZeroClipboard', function () {
    return {
		restrict: 'A',
		scope: {ngcZeroClipboardSettings:'=',
    			ngcZeroClipboardDataProvider: '&'},
        link:function (scope, element, attrs) {
    				
    		if (ZeroClipboard === undefined) {
    			alert('Please include the ZeroTable javascript files or remove the AngularJS directive from the element.');
    		} else if (attrs['ngcZeroClipboardDataProvider'] == null || attrs['ngcZeroClipboardDataProvider'] == '') {
    			alert('Attribute ngc-zero-clipboard-data-provider is missing.');
    		} else {
    			var settings = {
    				moviePath: 'ZeroClipboard.swf'
    			};
    			if (attrs['ngcZeroClipboardSettings'] != null && attrs['ngcZeroClipboardSettings'] != '') {
   					settings = $.extend(settings, scope.zeroClipboardSettings || {});
    			}
    			var dataProvider = scope.ngcZeroClipboardDataProvider;
    			ZeroClipboard.config(settings);
    			var client = new ZeroClipboard(element);
    			client.on('ready', function() {
        			client.on('copy', function(event) {
       					event.clipboardData.setData('text/plain', dataProvider());
        			});
        			element.on('$destroy', function(event) {
        				client.off('copy');
        				client.unclip(element);
        			});
    			});
    		}
        }
    };
});


/**
 * When a Bootstrap tab is used with AngularJS routing the location is changed when the tab is clicked.
 * The hashtag in the hyperlink is added to the location. This interferes with the AngularJS routing. To
 * prevent the click to interfere with the AngularJS routing add this directive on the hyperlink of the 
 * tabs by adding the attribute ngc-show-tab.
 */
application.directive('ngcShowTab', function () {
	return {
		link: function (scope, element, attrs) {
			element.click(function(e) {
				e.preventDefault();
				$(element).tab('show');
			});
		}
	};
});

/**
 * Directive for an animated progress bar with Bootstrap styling.
 */
application.directive('ngcProgressBar', function () {
    return {
		restrict: 'A',
		scope: {ngcProgressBarValue: '&', 
    			ngcProgressBarStep: '&',
    			ngcProgressBarInterval: '&',
    			ngcProgressBarClass: '&',
    			ngcProgressBarAction: '&'},
        link:function (scope, element, attrs) {

			function removeProgressBarClasses() {
    			$progressBar.removeClass('progress-bar-success');
    			$progressBar.removeClass('progress-bar-info');
    			$progressBar.removeClass('progress-bar-warning');
    			$progressBar.removeClass('progress-bar-danger');
			}

			function addProgressBarClass(progressBarClass) {
    			$progressBar.addClass(progressBarClass);
			}
			
			function increaseProgressBar() {
				progressBarValue = progressBarValue + step;
				if (progressBarValue > 100) {
					progressBarValue = 0;
				}
				$progressBar.width(progressBarValue + '%');
			}

			function startProgressBar() {
    			$progressBar.width(progressBarValue + '%');
    			i = setInterval(increaseProgressBar, interval);
			}
			
			function stopProgressBar() {
				clearInterval(i);
			}
			
			var step = 1;
			var progressBarValue = 0;
			var interval = 1000;
			var i = null;
			var clazz = '';
			var $progressBar = $('.progress-bar', element);
			
			if (attrs['ngcProgressBarValue'] != null && attrs['ngcProgressBarValue'] != '') {
    			progressBarValue = scope.ngcProgressBarValue();
	    		scope.$watch(scope.ngcProgressBarValue, function(newValue, oldValue) {
	    			if (newValue !== oldValue) {
	    				$progressBar.width(newValue + '%');
	    			}
	    		});
    		}
			if (attrs['ngcProgressBarStep'] != null && attrs['ngcProgressBarStep'] != '') {
				step = scope.ngcProgressBarStep();
	    		scope.$watch(scope.ngcProgressBarStep, function(newValue, oldValue) {
	    			if (newValue !== oldValue) {
	    				step = newValue;
	    			}
	    		});
    		}
			if (attrs['ngcProgressBarInterval'] != null && attrs['ngcProgressBarInterval'] != '') {
				interval = scope.ngcProgressBarInterval();
	    		scope.$watch(scope.ngcProgressBarInterval, function(newValue, oldValue) {
	    			if (newValue !== oldValue) {
		    			interval = newValue;
		    			clearInterval(i);
		    			i = setInterval(increaseProgressBar, interval);
	    			}
	    		});
    		}
			if (attrs['ngcProgressBarClass'] != null && attrs['ngcProgressBarClass'] != '') {
				clazz = scope.ngcProgressBarClass();
				removeProgressBarClasses();
				addProgressBarClass(clazz);
    			scope.$watch(scope.ngcProgressBarClass, function(newValue, oldValue) {
	    			if (newValue !== oldValue) {
	    				removeProgressBarClasses();
		    			addProgressBarClass(newValue);
	    			}
	    		});
    		}
			if (attrs['ngcProgressBarAction'] != null && attrs['ngcProgressBarAction'] != '') {
	    		scope.$watch(scope.ngcProgressBarAction, function(newValue, oldValue) {
	    			if (newValue !== oldValue) {
		    			if (newValue == 'start') {
		    				startProgressBar();
		    			} else if (newValue == 'stop') {
		    				stopProgressBar();
		    			}
	    			}
	    		});
    		} else {
    			startProgressBar();
    		}
			
        }
    };
});

/**
 * Directive to support the null value in the model behind a HTML select.
 * If the model contains a null value it is translated to an empty string
 * before rendering.
 * If the selected option contains an empty string it is translated to
 * null before the value is set in the model.
 * Add the following option to the select statement when this directive is
 * used, the text Empty can be changed to your liking:
 *   <option value="">Empty</option>
 */
application.directive('ngcSelectNullable', function() {
    return {
		restrict: 'A',
        require: 'ngModel',
        link: function(scope, element, attrs, ngModel) {

    		// View value to model value
	    	ngModel.$parsers.unshift(function (data) {
	    		if (data === '') {
	    			return null;
	    		}
	    		return data;
	        });
	
	    	// Model value to view value
			ngModel.$formatters.unshift(function (data) {
				if (data === null) {
					return '';
				}
				return data;
	        });
	            
        }
    };
});

/**
 * Directive to convert the string value of a select element to
 * an integer for the model.
 * 
 * If the view value is an empty string, null is set
 * in the model.
 * 
 * The priority of the directive is set so it runs after the build in one.
 */
application.directive('ngcSelectInteger', function() {
    return {
		priority: 1000,
		restrict: 'A',
        require: 'ngModel',
        link: function(scope, element, attrs, ngModel) {

    		// View value to model value
	    	ngModel.$parsers.push(function (data) {
	    		var value = null;
	    		if (data !== null) {
	    			if (typeof data === 'string' && data !== '') {
	    				value = parseInt(data, 10);
	    			} else if (typeof data === 'number') {
	    				value = data;
	    			}
	    		}
	    		return value;
	        });
	
			// Model value to view value
			ngModel.$formatters.push(function(data) {
				var value = '';
				if (data !== null && typeof data === 'number') {
					value = data + '';
				}
				return value;
			});
			
        }
    };
});

/**
 * Directive to convert the empty string value of a select element to
 * a null value for the model and vice versa.
 * 
 * Do not use it when the directive ngcSelectInteger is used on the same element.
 * 
 * The priority of the directive is set so it runs after the build in one.
 */
application.directive('ngcSelectString', function() {
    return {
		priority: 1000,
		restrict: 'A',
        require: 'ngModel',
        link: function(scope, element, attrs, ngModel) {

    		// View value to model value
	    	ngModel.$parsers.push(function (data) {
	    		var value = null;
	    		if (data !== null && data !== '') {
	    			value = data;
	    		}
	    		return value;
	        });
	
			// Model value to view value
			ngModel.$formatters.push(function(data) {
				var value = '';
				if (data !== null) {
					value = data;
				}
				return value;
			});
			
        }
    };
});

/**
 * Directive to convert the boolean values false and true of a checkbox 
 * element to their respective numbers 0 and 1 in the model and vice versa. 
 * If the model contains a null value, it is converted to false for
 * the view. If the checkbox contains a null value it is converted
 * to 0 for the model.
 * 
 * The priority of the directive is set so it runs after the build in one.
 */
application.directive('ngcCheckboxInteger', function() {
	return {
		priority: 1000,
		restrict: 'A',
		require: 'ngModel',
		link: function(scope, element, attrs, ngModel) {

			// View value to model value
			ngModel.$parsers.push(function(data) {
				var value = 0;
				if (data !== null && typeof data === 'boolean') {
					value = data ? 1 : 0;
				}
				return value;
			});

			// Model value to view value
			ngModel.$formatters.push(function(data) {
				var value = false;
				if (data !== null && typeof data === 'number') {
					value = data === 1 ? true : false;
				}
				return value;
			});
			
		}
	}
});

/**
 * Directive for formating a date model value to a view value and back
 * to the model value. Both values should be strings. Use it when both
 * values are in different formats.
 * For now it specific to:
 *  view: the models yyyy-MM-dd is represented in the local date format, for dutch: dd-MM-yyyy
 *  model: yyyy-MM-dd HH:mm:ss
 * 
 * This directive is created to use in input fields.
 * ngcViewFormat is the format for the view
 * ngcModelFormat is the format for the model
 */
application.directive('ngcDatefilter', function() {
	return {
		restrict: 'A',
		require: 'ngModel',
		link: function(scope, element, attrs, ngModel) {

    		// View value to model value
			ngModel.$parsers.push(function(data) {
				var value = null;
				if (data != null) {
					var dateParts = data.split("-");
					if (dateParts.length = 3) {
						value = data + ' 00:00:00';
					} else {
						ngModel.$setValidity('Incorrect date format.', false);
					}
				}
				return value;
			});
		
	    	// Model value to view value
			ngModel.$formatters.push(function(data) {
				var value = null;
				if (data != null) {
					var dateAndTimeParts = data.split(" ");
					if (dateAndTimeParts.length = 2) {
						value = dateAndTimeParts[0];
					}
				}
				return value;
			});
		}
	}
});

/**
 * Directive for formating a currency model value to a view value and back
 * to the model value. Both values should be strings.
 * If the $locale.NUMBER_FORMATS.DECIMAL_SEP is a comma the dot in a model
 * value is translated to a comma before returning it as a view value.
 * And if the view value contains a comma it is replaced by a dot before
 * storing it in the model
 * 
 * The view value will be outlined with zeros to two decimals without
 * a currency symbol. 
 */
application.directive('ngcCurrency', function($filter, $locale) {
	return {
		restrict: 'A',
		require: 'ngModel',
		link: function(scope, element, attrs, ngModel) {

    		// View value to model value
			ngModel.$parsers.push(function(data) {
				var value = null;
				if (data != null) {
	    			if (typeof data === 'string' && data !== '') {
	    				var v = data;
						if ($locale.NUMBER_FORMATS.DECIMAL_SEP == ',') {
							v = data.replace(',', '.');
						}
	    				value = parseFloat(v, 10);
	    			} else if (typeof data === 'number') {
	    				value = data;
	    			}
				}
				return value;
			});
		
	    	// Model value to view value
			ngModel.$formatters.push(function(data) {
				var value = null;
				if (data != null) {
					var v = data + '';
					if ($locale.NUMBER_FORMATS.DECIMAL_SEP == ',') {
						v.replace('.', ',');
					}
					value = $filter('ngcCurrency')(v, '');
				}
				return value;
			});
		}
	}
});

/**
 * Filter to return a list from the position start.
 * As opposed to AngularJS provided limitTo filter.
 */
application.filter('startFrom', function() {
	return function(input, start) {
		return input.slice(start);
	}
});

/**
 * Filter to return the currency.
 * Used as a single point for currency handling so it 
 * can be modified without much hassle.
 * 
 * Some times the currency is shown as &euro; instead
 * of the euro sign. This can be resolved by using 
 * the filter in the ng-bind-html attribute.
 * 
 * Angular sanitize is used to make the resulting HTML
 * trusted, otherwise in some cases thrust errors 
 * are thrown in the console. 
 */
application.filter('ngcCurrency', function($filter, $sce) {
	return function(input, type) {
		var t = (type === 'undefined') ? '' : type;
		return $sce.trustAsHtml($filter('currency')(input, t));
	}
});

/**
 * Filter to return a model date as a string or number.
 * The parameter date can be a string or number.
 * If the parameter date is a string the format should
 * be: dd-MM-yyyy HH:mm:ss.SSS
 * The parameter type can be string or number.
 * If the parameter type is a string then the model date 
 * is in the format: yyyy-MM-ddTHH:mm:ss.SSS
 * The parameter type is required because we do not know the
 * type in the model when the model value is initial null. 
 */
application.filter('ngcDateModel', function($filter) {
	
	function toDateModel(date, type) {
		var s = null;
		var t = !type ? 'string' : type;
		if (date != null && date != '') {
			if (t === 'string') {
				if (isNaN(date)) {
					var dateTimeParts = date.split(' ');
					var dateParts = dateTimeParts[0].split('-');
					s = dateParts[2] + '-' + dateParts[1] + '-' + dateParts[0] + 'T' + dateTimeParts[1];
				} else { // failsafe to handle a number if the type is defined as string
					var d = new Date(parseInt(date));
					s = d.getTime();
				}
			} else if (t === 'number') {
				if (isNaN(date)) { // failsafe to handle a string if the type is defined as number.
					var dateTimeParts = date.split(' ');
					var dateParts = dateTimeParts[0].split('-');
					var timeMillisecondParts = dateTimeParts[1].split('.');
					var timeParts = timeMillisecondParts[0].split(':');
					var d = new Date(dateParts[2], dateParts[1]-1, dateParts[0], timeParts[0], timeParts[1], timeParts[2], timeMillisecondParts[1]);
					s = d.getTime();
				} else {
					var d = new Date(parseInt(date));
					s = d.getTime();
				}
			}
		}
		return s;
	}
	
	return function(date, type) {
		return toDateModel(date, type);
	}
	
});

/**
 * Filter to return a display date in the 
 * format: dd-MM-yyyy HH:mm:ss.SSS
 * The parameter date can be a string or number.
 * If the parameter date is a string the format 
 * should be: yyyy-MM-ddTHH:mm:ss.SSS
 */
application.filter('ngcDate', function($filter) {
	
	function dateLpad(number) {
    	var s = !number ? '' : number.toString();
    	while (s.length < 2) {
    		s = '0' + s;
    	}
        return s;
	}

	function toDisplayDate(date) {
		var s = null;
		if (date != null && date != '') {
			if (date instanceof Date) {
		        s = dateLpad(date.getDate())
	            + '-' + dateLpad(date.getMonth() + 1)
	            + '-' + date.getFullYear()
	            + ' ' + dateLpad(date.getHours())
	            + ':' + dateLpad(date.getMinutes())
	            + ':' + dateLpad(date.getSeconds())
	            + '.' + String((date.getMilliseconds()/1000).toFixed(3)).slice(2, 5);
			} else if (typeof date == 'string') {
				if (isNaN(date)) {
					var dateTimeParts = date.split("T");
					var dateParts = dateTimeParts[0].split('-');
					var timeMillisecondParts = dateTimeParts[1].split('.');
					var timeParts = timeMillisecondParts[0].split(':');
					s = dateParts[2] + '-' + dateParts[1] + '-' + dateParts[0] + ' ' + timeParts[0] + ':' + timeParts[1] + ':' + timeParts[2] + '.' + timeMillisecondParts[1];
				} else {
					s = toDisplayDate(parseInt(date));
				}
			} else if (typeof date == 'number') {
				s = toDisplayDate(new Date(date));
			}
		}
		return s;
	}
	
	return function(date) {
		return toDisplayDate(date);
	}
	
});

/**
 * AngularJS module with basic support for translating a technical code to a
 * human readable string.
 */
(function(angular) {
	
	'use strict';

	var ngcMessages = angular.module('ngcMessages', []);
	
	/**
	* Factory providing a central point to ngcMessagesCache cache 
	* of message data, so this information is injectable through 
	* out the application.
	*/
	ngcMessages.factory('ngcMessagesCache', function NgcMessagesCache($cacheFactory) {
	
		var cacheName = 'ngcMessagesCache';
		
		return $cacheFactory(cacheName);
	
	});
	
	/**
	* Service for putting messages into the messages cache ngcMessagesCache
	* and getting a message from the cache.
	* 
	* Place the message into the cache by calling the load or put methode.
	* Retrieve a message by calling the get methode.
	* 
	* The load methode can be called with a URL. The URL should point to a
	* text file with entries containing a code and a text. The code
	* is the string prior to the first space and the text is the string
	* after the first space. Lines prefixed with a #-sign are comments
	* and are ignored. The filename should contain the language.
	* When the optional parameter onsuccess is set it will be called
	* when the loading is successfully finished. 
	* Example call: 
	*  ngcMessagesService.load('http://myhost/messages_nl.txt')
	*  ngcMessagesService.load('http://myhost/messages_en-us.txt')
	* Example call with onsuccess: 
	*  ngcMessagesService.load('http://myhost/messages_nl.txt', fucntion(language, messages) {
	*    // do something
	*  });
	* Example of the content in a file:
	* #some comment
	* myCode1 Text 1
	* myCode2 Text 2
	* 
	* The put methode can be called with an object for adding
	* multiple messages or with parameters to add a single message. 
	* For adding multiple messages the put methode should be called with
	* an object. This object should contain a property language 
	* and messages. The property language is a string and the 
	* property messages is an array of message objects. The message 
	* object should contain a property code and a property text.
	* Example multi messages: ngcMessagesService.put({
	*				'language': 'en-us', 
	*				'messages': [
	*								{'code': 'myCode1', 'text': 'Text 1'},
	*								{'code': 'myCode2', 'text': 'Text 2'}
	*							]
	*			});
	* For adding a single message the put methode should be called with the 
	* language, code and text.
	* Example single message: ngcMessagesService.put('en-us', 'myCode1', 'Text 1'); 
	* 
	* The get methode can be called with the code of the message and
	* will return the corresponding text. 
	*/
	ngcMessages.service('ngcMessagesService', function NgcMessagesService(
			$cacheFactory, $http, $locale, $interpolate, ngcMessagesCache) {

		// Default language
		var DEFAULT_LANGUAGE = 'default';
		
		// Initialize language to the language in the $locale.
		// Override/set the language by calling the setLanguage on this service.
		var language = $locale.id;
		
		/**
		 * Method to return the language in the url. If no language is detected
		 * the DEFAULT_LANGUAGE is returned. 
		 * Indeed regex is not used.
		 * Example: 
		 * For the url http://myhost/messages_nl.txt the return value is: nl
		 * For the url http://myhost/messages.txt the return value is: default
		 */
		function getLanguageFromUrl(url) {
			var language = url;
			var parts = null;
			// Part without query
			parts = language.split('?');
			language = parts[0];
			// Part after last slash
			parts = language.split('/');
			if (parts.length >= 1) {
				language = parts[parts.length-1];
			}
			// Part after last backslash
			parts = language.split('\\');
			if (parts.length >= 1) {
				language = parts[parts.length-1];
			}
			// Part before last period
			parts = language.split('.');
			language = parts[0];
			// Part after last underscore
			parts = language.split('_');
			if (parts.length >= 2) {
				language = parts[1];
			} else {
				language = DEFAULT_LANGUAGE;
			}
			return language;
		}
		
		/**
		 * Always returns an array from the cache.
		 * When the language is not found in the cache 
		 * it is added to the cache with an empty array.
		 */
		function getMessagesFromCache(language) {
			var messages = ngcMessagesCache.get(language);
			if (messages == null) {
				ngcMessagesCache.put(language, new Array());
				messages = ngcMessagesCache.get(language);
			}
			return messages;
		}
		
		/**
		 * Add messages from the array to the messages cache.
		 */
		function addMessagesMulti(o) {
			if (!o.language) {
				console.log("First parameter is an object and is missing the property language.");
			}
			if (!o.messages) {
				console.log("First parameter is an object and is missing the property messages.");
			}
			if (!o.messages instanceof Array) {
				console.log("First parameter is an object and the property messages is not an array.");
			}
			var language = o.language;
			var messages = getMessagesFromCache();
			var ms = o.messages;
			for (var i = 0; i < ms.length; i++) {
				var m = ms[i];
				if (!m.code) {
					console.log("Service method put requires property messages in the object of the input parameter.");
				}
				if (!m.text) {
					console.log("Service method put requires property messages in the object of the input parameter.");
				}
				messages[m.code] = {'code': m.code, 'text': m.text};
			}
			ngcMessagesCache.put(language, messages);
		}

		/**
		 * Add message to the messages cache.
		 */
		function addMessageSingle(language, code, text) {
			if (language == null || language == '') {
				console.log('Language parameter is empty.');
			}
			if (code == null || code == '') {
				console.log('Code parameter is empty.');
			}
			if (text == null || text == '') {
				console.log('Text parameter is empty.');
			}
			var messages = getMessagesFromCache(language);
			messages[code] = {'code': code, 'text': text};
		}

		/**
		 * Set the language for the service.
		 * This will override the language initialization 
		 * with the $locale.id.
		 */
		this.setLanguage = function(l) {
			language = l;
		}

		/**
		 * Load messages from an URL into the messages cache.
		 * When the optional parameter onsuccess is set it will 
		 * be called when the loading is successfully finished.
		 * The onsuccess parameter is a function with parameters
		 * language and messages. And can be ussed to do 
		 * action after loading like adding more messages or
		 * changing messages. 
		 */
		this.load = function(url, onsuccess) {
			if (url != null) {
				var language = getLanguageFromUrl(url);
				$http.get(url).success(function(data) {
					var lines = data.split('\n');
					var messages = getMessagesFromCache(language);
				    for(var i = 0; i < lines.length; i++){
				    	var line = lines[i];
				    	if (line.indexOf('#') != 0) {
					    	var pos = line.indexOf(' ');
					    	var code = line.substr(0, pos);
					    	var text = line.substr(pos + 1);
					    	messages[code] = {'code': code, 'text': text};
				    	}
				    }
				    if (onsuccess) {
				    	onsuccess(language, messages);
				    }
				});
			}
		}
		
		/**
		 * Add input to the messages cache.
		 */
		this.put = function(language, code, text) {
			if (language instanceof Object) {
				addMessagesMulti(language);
			} else {
				addMessageSingle(language, code, text);
			}
		}
		
		/**
		 * Returns the text from the message identified by the code
		 * and the current language. If the message is not found
		 * then the code is returned. 
		 * The parameters is an object and boilerplates 
		 * like {{<parameters.property>}} in the text are 
		 * replace with the value of the parameters.property. 
		 * Example for the use of parameters:
		 * Given the value for parmaeters: {'name': 'myName'}
		 * and the message text: Hello my name is {{name}}.
		 * The return value will be: Hello my name is myName.
		 */
		this.get = function(code, parameters) {
			var messages = ngcMessagesCache.get(language);
			// If the messages can not be found with the current language
			// then try if an entry exists with the language from
			// the DEFAULT_LANGUAGE.
			if (messages == null) {
				messages = ngcMessagesCache.get(DEFAULT_LANGUAGE);
			}
			var message = null;
			if (messages != undefined && messages != null) {
				message = messages[code];
			}
			var text = null;
			if (message == null) {
				text = code;
			} else {
				text = message.text;
			}
			if (parameters !== undefined && parameters != null) {
				text = $interpolate(text)(parameters);
			}
			return text;
		}

	});
	
	/**
	* Filter for translating a code to a human readable 
	* text with support for parameters.
	* If there is not a message found for the code,
	* then the code is returned.
	* 
	* Prior to the use of this filter all messages should
	* be registerd by the ngcMessagesService with:
	*  ngcMessagesService.put({'code':'myCode','text':'myText'}).
	*  
	* Example with no parameters:
	* {{myCode | ngcMessage}}
	* 
	* Example with to parameters in the text: 
	* {{myCode | ngcMessage:({"par1":"a", "par2":"b"})}}
	*/
	ngcMessages.filter('ngcMessage', function(ngcMessagesService) {
	
		return function(code, parameters) {
			return ngcMessagesService.get(code, parameters);
		}
		
	});

})(window.angular);
