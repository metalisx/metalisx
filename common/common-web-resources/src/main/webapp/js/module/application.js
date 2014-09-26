var application = angular.module('application', ['ngRoute', 'ngCookies']);

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
 * ngc-typeahead Constains the REST endpoint. The endponit should contain the 
 * string %QUERY, this will be replaced with the value entered in the input field. 
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

			var idKey = null;
			var valueKey = null;
			
			if (scope.ngcTypeahead == null || scope.ngcTypeahead == '') {
				alert('The ngc-typeahead attribute is missing the value of the REST endpoint.');
			}
			var callback = null;
			if (attrs['ngcTypeaheadCallback'] && attrs['ngcTypeaheadCallback'] != '') {
				callback = scope.ngcTypeaheadCallback;
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
			
			var engine = new Bloodhound({
				name: 'products',
				remote: scope.ngcTypeahead,
				limit: 10,
				minLength: 1,
				datumTokenizer: function(d) { 
					return Bloodhound.tokenizers.whitespace(d.val); 
				},
				queryTokenizer: Bloodhound.tokenizers.whitespace
			});
			engine.initialize();

			var $typeahead = null;
			var datasetName = 'dataset';
			function createTypeahead() {
				$typeahead = element.typeahead('destroy').typeahead(null, {
					name: datasetName,
					source: engine.ttAdapter(),
					displayKey: 'value'
				}).on('typeahead:selected', function(obj, datum, name) {
					callback({
						typeahead: $typeahead,
						datum: datum
					});
				});
				/* 
				 * Twitter typeahead has the style vertical-align set to top. When using
				 * Bootstrap the input is not inline with elements on the same
				 * line. But because Twitter typeahead has the style directly set on the 
				 * element, we can not overrule it with css. Therefore it is coded here.
				 */
				$typeahead.css('vertical-align', 'middle');
			}
			
			element.on('blur', function(event) {
				updateModel($typeahead.typeahead('val'));
			});
			
			scope.$watch('ngcTypeaheadModel', function(newValue, oldValue) {
				$typeahead.typeahead('val', newValue);
			});
			
			createTypeahead();
        }
    };
});

/**
 * Directive to append a color picker to the element.
 * Requires Spectrum colorpicker javascriptfile spectrum.js.
 */
application.directive('ngcColorPicker', function () {
    return {
		restrict: 'A',
		require: 'ngModel',
        link:function (scope, element, attrs, ngModel) {

			element.spectrum({
				preferredFormat: "hex",
				allowEmpty:true
			});

            scope.$watch('ngModel', function(newValue, oldValue) {
            	element.spectrum('set', ngModel.$viewValue || '');
			});
            
            element.on('change', function () {
            	scope.$apply(function () {
            		ngModel.$setViewValue(element.val());
            	});
            });              
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
 * Directive to append an icon to the field. When clicked a popup window
 * with a filebrowser is opened. On selecting a filename in the popup the
 * filename is set in the field an the model.
 * Set the ngc-filebrowser-url attribute to override the default url:
 * /v2/console/filebrowser
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
       			url: '/v2/console/filebrowser/filebrowser.html',
    			windowName: 'Filebrowser',
    			windowFeatures: 'width=' + width + ',height=' + height + ',left=' + left + ',top=' + top
    		}
    		if (attrs['ngcFilebrowserUrl'] && attrs['ngcFilebrowserUrl'] != '') {
				config.url = attrs['ngcFilebrowserUrl']; 
			}
    		config.url = config.url + '?caller=custom&callback=ngcFilebrowserCallback';
    		$filebrowserButton = $('<i class="icon-search"></i>');
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
 * the ngc-ckeditor-save attribute on the element. If not set the directive
 * will look for the method editorSave in the parent scope and if found this
 * method is used otherwise nothing will happen when the save button is 
 * clicked. The save method does not take any parameters because the value 
 * is in the model.
 */
application.directive('ngcCkeditor', function () {
	
    return {
		restrict: 'A',
		require: 'ngModel',
		scope: {saveMethod:'&ngcCkeditorSaveMethod'},
        link:function (scope, element, attrs, ngModel) {
    	
    		function addSourceModeEvent(editor) {
				// Adding key up event to track changes if the editor in source mode
    			$('textarea.cke_source', element.parent()).on('keyup', function() {
					ngModel.$setViewValue(editor.getData());
					scope.$apply();
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
    			if (attrs['ngcCkeditorMode'] && attrs['ngcCkeditorMode'] != '') {
    				var mode = attrs['ngcCkeditorMode'];
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
    				saveMethod = scope.saveMethod;
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
 * Requires jQuery datatables.js 1.9.2 javascript files.
 */
application.directive('ngcDataTable', function () {
	
    return {
		restrict: 'A',
		scope: {dataTableSettings:'=ngcDataTableSettings',
    			dataTableFilter: '=ngcDataTableFilter'},
        link:function (scope, element, attrs) {
    		if ($.fn.dataTable === 'undefined') {
    			alert('Please include the DataTables javascript files or remove the AngularJS directive from the element.');
    		} else {
    			if (scope.dataTableSettings == null || scope.dataTableSettings == '') {
    				alert('The ngc-data-table-settings attribute is missing.');
    			} else {
    				var filter = (scope.dataTableFilter != null ? scope.dataTableFilter : null);
	    			var datatable = element.metalisxDataTable(filter, scope.dataTableSettings);
	    			// Destroy the datatable when the element is removed from the DOM.
	    			// This is required when the ng-view is used.
	    			element.bind("$destroy", function() {
	        			datatable.fnDestroy();
	    	        });
    			}
    		}
        }
    };
});
