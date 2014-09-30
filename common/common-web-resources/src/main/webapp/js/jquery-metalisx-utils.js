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
 * jQuery plugin to render messages.
 */
(function($){

	/**
	 * The first parameter can contain:
	 *  - a text
	 *  - a message object containing properties id, text, detail and level
	 *  - a list of message objects. 
	 * The id in the object should be the id of an existing HTML element.
	 * The level in the object takes precedence over the level in the options. 
	 * 
	 * The second parameter contians options. The properties for the options argument are:
	 *  - level
	 *  - clean
	 *  - location
	 *  - containerId
	 * level Will set the style of the messages. Possible values for level are success, error, 
	 * info and any other value will use the default styling.
	 * clean Will keep or remove previous messages. If true it will remove all previous
	 * messages, if false it will keep the previous messages.
	 * location Will tell if the text should be placed as first or last element in the
	 * container. Possible values are first and last. If first then the text is 
	 * added as first element. If last then the text is added as last element. Only
	 * useful if the clean is set to false to keep previous messages.
	 * containerId Contains the id of the HTML object in which the message should be placed
	 * if the message does not contain an id.
	 * 
	 * If the message contains an id of an HTML element and the HTML element does not contain 
	 * the messagesContainerClass, the parent of the HTML element is search for an HTML element 
	 * with the messagesContainerClass. If found it will be the HTML element in which the
	 * message is placed if it is not found then it will be placed in the HTML element with the id.
	 * When using the id of an INPUT elementd, add a DIV element after it with the class name in 
	 * the messagesContainerClass setting. As an alternative you could set the id of the DIV element 
	 * in the message. If you do not, the message is placed in the INPUT element which is not 
	 * correct and you miss the message. Use this solution if you want a message coupled to another
	 * element.
	 * 
	 * Requires the Twitter Bootstrap styles alert-success, alert-danger and alert-info or you
	 * need to define them yourself.
	 */
	$.metalisxMessages = function(value, options) {

		var settings = $.extend(true, {
			level: 'info',
			clean: true,
			location: 'first',
			messagesContainerId: 'messagesContainer',
			messagesContainerClass: 'messagesContainer',
			messagesContainerForElementClass: 'messagesContainerForElement',
			messageInnerContainerClass: 'messageInnerContainer',
			messageDetailClass: 'messageDetail',
			messageIframeClass: 'messageIframe',
			messageInnerContainerHeight: '400px'
		}, options || {});
		
		var text = null;
		var detail = null;
		var level = null;
		// List to keep track of already cleaned containers.
		var processedContainers = new Array();

		function render(message) {
			if (message.message == null || message.message == '') {
				return;
			}

			var containerId = null;
			// If there is an id but not an element with the id then the default is used.
			if (message.id != null && $('#' + message.id).size() != 0) {
				containerId = message.id;
			} else {
				containerId = settings.messagesContainerId;
			}
			var text = message.message;
			var detail = null;
			if (message.detail != null) {
				detail = message.detail;
			}
			var level = null;
			if (message.level != null) {
				level = message.level;
			} else {
				level = settings.level;
			}

			$container = $('#' + containerId);
			if ($container.size() == 0) {
				if (console.log != undefined) {
					console.log('There is no HTML object container containing the id attribute with value ' + 
							containerId + ".");
					console.log(message);
				}
			}

			// If the DOM element does not have the class in messagesContainerClass setting then
			// the element is search in the parent of the DOM element.
			// If not found a new container is added with the messagesContainerClass after the container.
			if (!$container.hasClass(settings.messagesContainerClass)) {
				var $newContainer = $container.parent().find('.' + settings.messagesContainerClass);
				if ($newContainer.size() == 0) {
					$newContainer = $('<div></div>');
					$newContainer.addClass(settings.messagesContainerClass);
					$newContainer.addClass(settings.messagesContainerForElementClass);
					$container.after($newContainer);
				}
				$container = $newContainer;
			}
			
			if (settings.clean) {
				var processed = false;
				$.each(processedContainers, function(index, item) {
					if (item == containerId) {
						processed = true;
					}
				});
				if (!processed) {
					$container.empty();
					processedContainers.push(containerId);
				}
			}
			
			var $message = $('<div/>').addClass('alert');
			if (settings.location == 'first') {
				$container.prepend($message);
			} else if (settings.location == 'last') {
				$container.append($message);
			} else {
				alert('Unknown location ' + settings.location);
			}
			
			if (level.toLowerCase() == 'success') {
				$message.addClass('alert-success');
			} else if (level.toLowerCase() == 'error') {
				$message.addClass('alert-danger');
			} else if (level.toLowerCase() == 'info') {
				$message.addClass('alert-info');
			}

			$message.append(text);
			if (detail) {
				$detail = $('<a class="' + settings.messageDetailClass + '" href="#">Detail</a>')
					.click(function() {
						if ($('.' + settings.messageIframeClass, $container).size() == 0) {
							$message.append('<iframe width="100%" height="' + settings.messageInnerContainerHeight + 
										'" class="' + settings.messageIframeClass + '"/>');
							$('.' + settings.messageIframeClass, $message).contents().find('html').html(detail);
						} else {
							$('.' + settings.messageIframeClass, $message).remove();
						}
					});
				$message.append($detail);
			}
			$container.show();
			
		}
		
		if (value instanceof Array) {
			$.each(value, function(index, message) {
				render(message);
			});
		} else if (value instanceof Object) {
			render(value);
		} else {
			var message = {};
			message.id = settings.id;
			message.message = value;
			message.level = settings.level;
			render(message);
		}

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
			messagesContainerId: 'messagesContainer',
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

		var messagesContainer = $('#' + settings.messagesContainerId);
		if (messagesContainer.size() == 1) {
			messagesContainer.hide();
			messagesContainer.empty();
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
			        	$.metalisxMessages({id: settings.messagesContainerId, message: 'Parsing the body failed. ' + body, level: 'error'});
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
 * The options parameter contains a property messagesContainer. The value 
 * is an id of an HTML object like a DIV. The value is by default messagesContainer. 
 * This means a container(DIV) with id messagesContainer should be included in 
 * the HTML page. A JSON messages or JSON exception object produces by the
 * Ajax call are rendered in this container. If the container can not be 
 * found alerts are shown with the javascript alert function.
 * 
 * The options parameter contains a property cleanMessagesContainer. If true
 * the messagesContainer will be hidden and the content will be removed. Also
 * all containers containing the class messagesContainerClass will be hidden and
 * the content will be removed. If false the messagesContainer and all conatiners
 * containing the messagesContainerClass will be kept visible if they are already 
 * visible. The default value is true.
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
			messagesContainerId: 'messagesContainer',
			messagesContainerClass: 'messagesContainer',
			cleanMessagesContainer: true,
			handleResponseMessages: true
		}, options || {});

		var messagesContainer = $('#' + settings.messagesContainerId);
		if (messagesContainer.size() == 1) {
			if (settings.cleanMessagesContainer) {
				messagesContainer.hide();
				messagesContainer.empty();
				// clean up all messages containers containing the class messageContainerClass
				var messagesContainers = $('.' + settings.messagesContainerClass);
				messagesContainers.each(function() {
					$(this).hide();
					$(this).empty();
				});
			}
		} else {
			if (console.log != undefined) {
				console.log('There is no HTML object container containing the id attribute with value ' + 
						settings.messagesContainerId + ".");
				console.log(message);
			}
		}

		if (!url || url == null || url == '') {
			$.metalisxMessages({id: settings.messagesContainerId, message: 'Url for the data source is not set.', level: 'error'});
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
					if (settings.handleResponseMessages && result.messages && result.messages.length > 0) {
						$.metalisxMessages(result.messages);
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
        	if (jqXHR.status != 0) { // Canceled requests are not processed.
				var message = {};
				message.id = settings.messagesContainerId;
				message.message = textStatus + (errorThrown ? ': ' + errorThrown : '');
				if (jqXHR.responseText.indexOf('body') > 0) {
					message.detail = jqXHR.responseText;
				}
				message.level = 'error';
				$.metalisxMessages(message);
				$.metalisxUnblock();
        	}
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

