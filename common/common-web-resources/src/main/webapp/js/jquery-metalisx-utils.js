/**
 * Utils jQuery plugin.
 */

/**
 * Simple URL value getter methods. This is an implementation which is suitable in 
 * simple cases. If more functionality is required look at Ben Alman jQuery 
 * BBQ plugin.
 */
(function($){
	
	$.urlValuesToObject = function() {
		var object = {};
		var values = $.getUrlValues();
		for (var key in values) {
			object[key] = values[key];
		}
		return object;
	};

	$.getUrlValue = function(key) {
		var values = $.getUrlValues();
		if (values == null) {
			return null;
		}
		if (key in values) {
			return values[key];
		}
		return null;
	};
	
	$.getUrlValues = function() {
	    var values = [];
	    if (window.location.search != '') {
		    var params = window.location.search.slice(1).split('&');
		    for(var key in params) {
		    	var valuePair = params[key].split('=');
		    	if (valuePair[1] != 'null') {
		    		values[valuePair[0]] = unescape(valuePair[1]);
		    	}
		    }
	    }
	    return values;
	};

})(jQuery);

/**
 * jQuery plugin to block and unblock user input.
 * 
 * Required CCS class can be found in the section "Block input" 
 * in the stylesheet file common.css. 
 */
(function($){
	
	$.metalisxBlock = function(options) {

		var settings = $.extend(true, {
			offset: 58,
			blockId: 'block',
			blockClass: 'block'
		}, options || {});

		var $div = $('<div id="' + settings.blockId + '" class="' + settings.blockClass + '">');
		$('body').append($div);
	    $div.show();
	};

	$.metalisxUnblock = function(options) {

		var settings = $.extend(true, {
			blockId: 'block'
		}, options || {});

		$('#' + settings.blockId).remove();
	};

})(jQuery);

/**
 * jQuery plugin to render alerts.
 * Contains two methods:
 *  - metalisxAjaxAlert to render an error returned by an Ajax call
 *  - metalisxAlert to render a text 
 * Requires:
 *  - the Twitter Bootstrap alert styles.
 * The properties for the options argument are:
 *  - level
 *  - clean
 *  - location
 * type Will set the level of alert. Possible values for type are success, error, 
 * info and any other value will use the default styling.
 * clean Will keep or remove previous alerts. If true it will remove all previous
 * alerts, if false it will keep the previous alerts.
 * location Will tell if the text should be placed as first or last element in the
 * container. Possible values are first and last. If first then the text is 
 * added as first element. If last then the text is added as last element. Only
 * useful if the clean is set to false to keep previous alerts. 
 */
(function($){

	$.fn.metalisxAjaxAlert = function(jqXHR, textStatus, errorThrown, options) {
		
		var settings = $.extend(true, {
			type: 'info',
			clean: true,
			location: 'first',
			alertInnerContainerClass: 'alertInnerContainer',
			alertDetailClass: 'alertDetail',
			alertIframeClass: 'alertIframe',
			alertInnerContainerHeight: '400px'
		}, options || {});
		
		var $this = $(this);
		if (errorThrown) {
			var $error = $('<div class="' + settings.alertInnerContainerClass + '" width="100%"/>')
				.append('Request failed: ' + textStatus + ' ' + errorThrown + '&nbsp;&nbsp;');
			if (jqXHR.responseText.indexOf('body') > 0) {
				$errorDetail = $('<a class="' + settings.alertDetailClass + '" href="#">Detail</a>')
					.click(function() {
						if ($('.' + settings.alertIframeClass, $this).size() == 0) {
							$('.' + settings.alertInnerContainerClass, $this).append('<br/>');
							$('.' + settings.alertInnerContainerClass, $this)
								.append('<iframe width="100%" height="' + settings.alertInnerContainerHeight + 
										'" class="' + settings.alertIframeClass + '"/>');
							$('.' + settings.alertIframeClass, $this).contents().find('html').html(jqXHR.responseText);
						} else {
							$('.' + settings.alertInnerContainerClass + ' br', $this).remove();
							$('.' + settings.alertIframeClass, $this).remove();
						}
					});
				$error.append($errorDetail);
			}
			$this.metalisxAlert($error, {type: 'error'});
		}
	};
	
	$.fn.metalisxAlert = function(text, options) {

		var settings = $.extend(true, {
			level: 'info',
			clean: true,
			location: 'first'
		}, options || {});
		
		var $this = this;

		if (settings.clean) {
			$this.empty();
		}
		
		$alert = $('<div/>').addClass('alert');
		if (settings.location == 'first') {
			$this.prepend($alert);
		} else if (settings.location == 'last') {
			$this.append($alert);
		} else {
			alert('Unknown location ' + settings.location);
		}
		$alert.removeClass('alert-success');
		$alert.removeClass('alert-error');
		$alert.removeClass('alert-info');
		
		if (settings.level.toLowerCase() == 'success') {
			$alert.addClass('alert-success');
		} else if (settings.level.toLowerCase() == 'error') {
			$alert.addClass('alert-error');
		} else if (settings.level.toLowerCase() == 'info') {
			$alert.addClass('alert-info');
		}

		$alert.append(text);
		$this.show();

		return this;
		
	};

})(jQuery);

/**
 * jQuery plugin for file upload.
 * 
 * The upload button will submit the closest form when uploading.
 * 
 * The default styling of the input file type is different
 * across browsers. This plugin and the accompanied CSS styles
 * renders a common style across browsers.
 * 
 * Expected response from the server is a body containing a JSON object.
 * This object needs to contain a property: uploadFiles. The uploadFiles
 * is a property containing a list of items. If the default renderItem
 * is used the item should contain filename, contentTYpe and size. 
 * A response example:
 *  {"uploadFiles":[{"filename":"thefile.txt","contentType":"text/plain","size":10}]}
 *  
 * If the list items should be rendered otherwise, then override
 * the renderItem method in the options parameter. The first
 * parameter in the method represents the item in the uploadFiles 
 * property.
 * 
 * Required CCS class can be found in the section "File upload" 
 * in the stylesheet file common.css. 
 */
(function($){
	
	$.fn.metalisxFileupload = function(options) {
		var settings = null;
		settings = $.extend({
			action: null,
			method: 'POST',
			encoding: 'multipart/form-data',
			enctype: 'multipart/form-data',
			acceptCharset: 'UTF-8',
			fileUploadInfoId: 'fileUploadInfo',
			fileChooserContainerClass: 'fileChooserContainer',
			iframeId: 'upload_iframe',
			fileChooserFilenameClass: 'fileChooserFilename',
			fileUploadButtonClass: 'fileUploadButton',
			noFileSeletedMessage: 'Select a file',
			chooseAFileMessage: 'Choose a file...',
			alertContainerId: 'alertContainer',
			renderRow: function(item) {
				return '<tr><td class="filename">' + item.filename + '</td>' + 
					'<td class="contentType">' + item.contentType + '</td>' +
					'<td class="size">' + item.size + '</td></tr>';
			},
			onsuccess: function(data) {
				if (data && data.uploadFiles && data.uploadFiles.length > 0) {
					var $fileUploadInfo = $('#' + settings.fileUploadInfoId, $this);
					if ($fileUploadInfo.size() == 1) {
						var $tbody = $('table tbody', $fileUploadInfo); 
						$.each(data.uploadFiles, function(index, item) {
							$tbody.append(settings.renderRow(item));
						});
					}
				}
			}
		}, options || {});

		var $this = $(this);
		var $form = $this.closest('form');
		var target = settings.iframeId + (this.id ? '_' + this.id : '');

		var alertContainer = $('#' + settings.alertContainerId);
		if (alertContainer.size() == 1) {
			alertContainer.hide();
			alertContainer.empty();
		}

		function upload() {
			var $div = $("<div/>")
				.css('display','none');
			var $iframe = $('<iframe/>').css({
					'width': '0px',
					'height': '0px',
					'border': '0px'
				}).attr({
					'width': '0px',
					'height': '0px',
					'border': '0px',
					'name': target,
					'id': target
				});
			$div.append($iframe);
			$('body').append($div);
			// After the iframe is attached the load event is added 
			// to the iframe to handle the returned JSON object in 
			// the body of the iframe.
			$iframe.load(function() {
				var body = $($iframe).contents().find('body').html();
				if (body) {
					try {
						var data = $.parseJSON(body);
						settings.onsuccess(data);
					} catch (error) {
						if (alertContainer.size() == 1) {
				        	alertContainer.show();
				        	alertContainer.metalisxAlert('Parsing the body failed. ' + body, {level: 'error'});
						} else {
							alert('Parsing the body failed. ' + body);
						}
					}
				}
				$div.remove();
			});
		}

		function render() {
			$form.attr({
				'target': target,
				'action': settings.action,
				'method': settings.method,
				'encoding': settings.encoding,
				'enctype': settings.enctype,
				'accept-charset': settings.acceptCharset
			});
			$('.' + settings.fileChooserFilenameClass, $this).text(settings.chooseAFileMessage);
		}

		function bindings() {
			$this.closest('form').submit(function(){
				if ($('.' + settings.fileChooserContainerClass +  ' input:file', $this).val() != '') {
					upload();
				} else {
					alert(settings.noFileSeletedMessage);
					return false;
				}
			});
			$('.' + settings.fileChooserContainerClass + ' input:file', $this).change(function(){
				var filename = $(this).val();
				if (filename == '') {
					filename = settings.chooseAFileMessage;
				} else {
					filename = $(this).val().substr($(this).val().lastIndexOf("\\") + 1);
				}
				$('.' + settings.fileChooserFilenameClass, $this).text(filename);
			});
			$('.' + settings.fileUploadButtonClass, $this).click(function() {
				$this.closest('form').submit();
				return false;
			});
		}
		
		render();
		if (settings.action != null) {
			bindings();
		} else {
			alert('action Property is required in options parameter.');
		}

	};
	
})(jQuery);

/**
 * jQuery data provider plugin.
 * 
 * Retrieves data through AJAX calls from a backend service.
 * 
 * The options parameter contains a property alertContainer. The value 
 * is an id of an HTML object like a DIV. The value is by default alertContainer. 
 * This means a container(DIV) with id alertContainer should be included in 
 * the HTML page. A JSON messages or JSON exception object produces by the
 * Ajax call are rendered in this container. If the container can not be 
 * found alerts are shown with the javascript alert function.
 * 
 * The options parameter contains a property cleanAlertContainer. If true
 * the alertContainer will be hidden and the content will be removed. If 
 * false the alertContainer will be kept visible if it is already visible 
 * and the content remains in the alertContainer. The default value is true.
 * 
 * The JSON structure for messages is, multiple message object are possible: 
 *  {"messages":[{"message":"The message.","level":"error"}]}
 * Level can be: success, error or info.
 * 
 * The JSON structure for an exception is {"exception": "The exception"} and
 * is shown with level error.
 * 
 */
(function($) {

	$.metalisxDataProvider = {

		post: function(url, requestData, options) {
			var settings = $.extend({
				type: 'POST',
				contentType: 'application/json; charset=UTF-8',
				dataType: 'json'
			}, options || {});
			dataProvider(url, JSON.stringify(requestData), settings);
		},
		
		put: function(url, requestData, options) {
			var settings = $.extend({
				type: 'PUT',
				contentType: 'application/json; charset=UTF-8',
				dataType: 'json'
			}, options || {});
			dataProvider(url, JSON.stringify(requestData), settings);
		},

		get: function(url, requestData, options) {
			var settings = $.extend({
				type: 'GET',
				contentType: null,
				dataType: 'json'
			}, options || {});
			dataProvider(url, requestData, settings);
		},

		del: function(url, requestData, options) {
			var settings = $.extend({
				type: 'DELETE',
				contentType: null,
				dataType: 'json'
			}, options || {});
			dataProvider(url, requestData, settings);
		},

		head: function(url, requestData, options) {
			var settings = $.extend({
				type: 'HEAD',
				contentType: null,
				dataType: null
			}, options || {});
			dataProvider(url, requestData, settings);
		},

		form: function(url, requestData, options) {
			var settings = $.extend({
				type: 'POST',
				contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
				dataType: 'json'
			}, options || {});
			dataProvider(url, requestData, settings);
		}
	};
	
	function dataProvider(url, requestData, options) {

		var settings = $.extend({
			type: 'GET',
			contentType: null,
			dataType: null,
			cache: false,
			onsuccess: function(result) {},
			alertContainerId: 'alertContainer',
			cleanAlertContainer: true,
			handleResponseMessages: true
		}, options || {});

		var alertContainer = $('#' + settings.alertContainerId);
		if (alertContainer.size() == 1) {
			if (settings.cleanAlertContainer) {
				alertContainer.hide();
				alertContainer.empty();
			}
		}

		if (!url || url == null || url == '') {
			if (alertContainer.size() == 1) {
				alertContainer.show();
				alertContainer.metalisxAlert('Url for the data source is not set.', {level: 'error'});
			} else {
				alert('Url for the data source is not set.');
			}
			return this;
		}

		$.metalisxBlock();
		
		var jqxhr = null;
		jqxhr = $.ajax({
			type: settings.type,
			url: url,
			data: requestData,
			cache: settings.cache,
			success: function(result) {
				var executeOnsuccess = true;
				if (result) {
					if (result.exception) {
						if (alertContainer.size() == 1) {
							alertContainer.show();
							alertContainer.metalisxAlert(result.exception, {level: 'error', clean: false});
						} else {
							alert(result.exception);
						}
					} else if (settings.handleResponseMessages && result.messages && result.messages.length > 0) {
						if (alertContainer.size() == 1) {
							$.each(result.messages, function(index, message) {
								if (message.message) {
									var messageLevel = message.level ? message.level : 'error';
									alertContainer.metalisxAlert(message.message, {level: messageLevel, clean: false});
								}
							});
						} else {
							$.each(result.messages, function(item, message) {
								if (message.message) {
									alert(message.message);
								}
							});
						}
						$.each(result.messages, function(index, message) {
							if (message.message && message.level && message.level.toLowerCase() == 'error') {
								executeOnsuccess = false;
							}
						});
					}
				} else if (settings.type.toUpperCase() != 'HEAD') {
					executeOnsuccess = false;
				}
				if (executeOnsuccess) {
					settings.onsuccess(result, jqxhr);
				}
				$.metalisxUnblock();
			},
			contentType: settings.contentType,
			dataType: settings.dataType
		}).fail(function(jqXHR, textStatus, errorThrown) {
			if (alertContainer.size() == 1) {
	        	alertContainer.show();
	        	alertContainer.metalisxAjaxAlert(jqXHR, textStatus, errorThrown);
			} else {
				var error = "Request failed: " + textStatus + " " + errorThrown;
				alert(error);
			}
			$.metalisxUnblock();
		});
		
		return this;

	};
	
}) (jQuery);

/**
 * jQuery plugin for utility functions.
 */
(function($){

	function dateLpad(number) {
    	var s = !number ? '' : number.toString();
    	while (s.length < 2) {
    		s = '0' + s;
    	}
        return s;
	}

	/** Create own namespace. */
	$.metalisxUtils = {
			
		/** Escape html characters. */
		escapeHtml: function(text) {
			return text
		    .replace(/&/g, '&amp;')
		    .replace(/>/g, '&gt;')
		    .replace(/</g, '&lt;')
		    .replace(/"/g, '&quot;')
		    .replace(/'/g, '&quot;');
		},

		displayDateToIsoDateAsString: function isodateAsStringToDate(value) {
			if (!value) return null;
			var dateTimeParts = value.split(' ');
			var dateParts = dateTimeParts[0].split('-');
			return dateParts[2] + '-' + dateParts[1] + '-' + dateParts[0] + 'T' + dateTimeParts[1];
		},

		isoDateAsStringtoDate: function isodateAsStringToDate(value) {
			if (!value) return null;
			var dateTimeParts = value.split("T");
			var dateParts = dateTimeParts[0].split('-');
			var timeMillisecondParts = dateTimeParts[1].split('.');
			var timeParts = timeMillisecondParts[0].split(':');
			return date = new Date(dateParts[0], dateParts[1]-1, dateParts[2], timeParts[0], timeParts[1], timeParts[2], timeMillisecondParts[1]);
		},

		isoDateAsStringtoDisplayDate: function isoDateAsStringtoDisplayDate(value) {
			if (!value) return null;
			var dateTimeParts = value.split("T");
			var dateParts = dateTimeParts[0].split('-');
			var timeMillisecondParts = dateTimeParts[1].split('.');
			var timeParts = timeMillisecondParts[0].split(':');
			return dateParts[2] + '-' + dateParts[1] + '-' + dateParts[0] + ' ' + timeParts[0] + ':' + timeParts[1] + ':' + timeParts[2] + '.' + timeMillisecondParts[1];
		},

		dateToIsoDateAsString: function(date) {
	        return date.getFullYear()
	            + '-' + dateLpad(date.getMonth() + 1)
	            + '-' + dateLpad(date.getDate())
	            + 'T' + dateLpad(date.getHours())
	            + ':' + dateLpad(date.getMinutes())
	            + ':' + dateLpad(date.getSeconds())
	            + '.' + String((date.getMilliseconds()/1000).toFixed(3)).slice(2, 5)
	            ;//+ 'Z';
		},

		dateToDisplayDate: function(date) {
	        return date.getDate()
	            + '-' + dateLpad(date.getMonth() + 1)
	            + '-' + date.getFullYear()
	            + ' ' + dateLpad(date.getHours())
	            + ':' + dateLpad(date.getMinutes())
	            + ':' + dateLpad(date.getSeconds())
	            + '.' + String((date.getMilliseconds()/1000).toFixed(3)).slice(2, 5);
		}
	};

})(jQuery);

