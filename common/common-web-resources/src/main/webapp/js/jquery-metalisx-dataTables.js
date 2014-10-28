/**
 * jQuery plugin for a default configured DataTables styled with bootstrap.
 * 
 * The method metalisxDataTable excepts the following parameters in the same
 * order:
 *  - filter
 *  - options
 * 
 * The filter parameter can contain an object with any kind of properties. This 
 * object is added to the pageContext and posted to the server.
 * 
 * The posted object structure to the server is:
 * {
 * 		filter: <only present if there is a filter object>,
 * 		orderBy: {
 * 			columnName: '<column name>',
 * 			columnDirection: '<column direction>'
 *  	},
 *		limit: {
 *			start: <start>,
 *			count: <count>
 *		}
 * }
 * Note: if in the options.dataTableSettings.paging is set to false
 * the limit.count will be set to -1. Indicating no paging is used. 
 * 
 * The recieved object structure from the server is:
 * {
 * 		items: <list of objects>,
 * 		totalNumberOfRows: <total number of rows>
 * 		messages: <list of messages>
 * }
 * Messages in the messages property are shown in the HTML container
 * with the id specified in the messagesContainerId property of the options.
 * This can be used to show errors from the back end in the view.
 * 
 * The options parameter provides the plugin with some settings. One of
 * the properties in options is dataTableSettings where you can set the
 * defaults for the DataTable plugin.
 *
 * The only required setting is the URL from which the data is retrieved. Set 
 * the URL in the options object parameter as dataTableSettings.ajax. 
 * Example: {
 * 				dataTableSettings: {
 * 										"ajax": myUrl
 *									}
 *			}
 * 
 * This plugin uses for the Ajax request the type POST, content type application/json and
 * dataType JSON. This is the most convenient way to handle the AJAX data on the 
 * server side. Because the server side can use a framework to automatically transform the 
 * received JSON object.
 * 
 * The options parameter has a property renderDetail. It is default null
 * but when set it needs to be a function with one parameter, this parameter
 * contains the object for the row. Example: renderDetail(aData). When a user 
 * clicks on a row a child row is added below this row and the renderDetail function 
 * is called to populate the child row.
 * 
 * The options parameter has a property onsuccess. It is default null
 * and can contain a method without parameters. It will run after the
 * first Ajax call to retrieve data from the server.
 * 
 * The options parameter has a property onsuccessAjax. It is default null
 * and can contain a method without parameters. It will run after every
 * Ajax call to retrieve data from the server.
 */
(function($){

	$.fn.metalisxDataTable = function(filter, options) {
		var url = options.dataTableSettings.ajax;
		var dataTable = null;
		var runOnsuccess = true;
		var settings = $.extend(true, {
			messagesContainerId: 'messagesContainer',
			onsuccess: null,
			onsuccessAjax: null,
			onsuccessRow: null,
			onRowClick: null,
			renderDetail: null, 
			dataTableSettings: {
				"autoWidth": false,
				"destroy": true,
				"processing": true,
				"serverSide": true,
				"searching": false,
				"orderClasses": false,
				"paging": true,
				"pagingType": "full_numbers", 
				"createdRow" : function(row, data, dataIndex) {
					$('a', $(row)).click(function(event) {
						event.stopImmediatePropagation();
					});
					if (settings.renderDetail) {
						$(row).click(function() {
							var child = dataTable.api().row($(row)).child;
							if (child.isShown()) {
								child.remove();
							} else {
								var nodes = dataTable.api().rows().nodes();
								for (var i=0; i < nodes.length; i++) {
									if (dataTable.api().row($(nodes[i])).child.isShown()) {
										dataTable.api().row($(nodes[i])).child.remove();
									}
								}
								var $detailContainer = $('<div></div>').addClass("detail");
								child($detailContainer);
								child.show();
								settings.renderDetail($detailContainer, row, data, dataIndex);
							}
						});
					}
					if (settings.onsuccessRow) {
						settings.onsuccessRow(row, data, dataIndex);
					}
					if (settings.onRowClick) {
						$(row).click(function(event) {
							settings.onRowClick(row, data, dataIndex);
						});
					}
				}
			}
		}, options || {});

		settings.dataTableSettings.ajax = function(data, callback, ajaxSettings) {
			messagesContainer.hide();
			messagesContainer.empty();

			var innerCallback = function(result) {
        		if (result.messages) {
        			$.metalisxMessages(result.messages, {messagesContainerId: settings.messagesContainerId});
					$('#' + id + '_processing', $this.parent()).hide();
				} else {
					var dataTableResult = pageToDataTableResult(result);
					callback(dataTableResult);
					if (settings.onsuccess && runOnsuccess) {
						runOnsuccess = false;
						settings.onsuccess();
					}
					if (settings.onsuccessAjax) {
						settings.onsuccessAjax($this);
					}
				}
        	};

        	var pageContext = dataTableContextToPageContext(data, filter);
        	ajaxSettings.jqXHR = $.ajax({
					"type": "POST",
					"contentType": "application/json",
					"dataType": "json",
					"url": url,
					"data": JSON.stringify(pageContext),
					"success": innerCallback}
            ).fail(function(jqXHR, textStatus, errorThrown) {
            	if (jqXHR.status != 0) { // Canceled requests are not processed. 
		        	if ($.metalisxMessages === undefined) {
		        		alert(textStatus + (errorThrown ? ': ' + errorThrown : ''));
		        	} else {
		    			var message = {};
		    			message.id = settings.messagesContainerId;
		    			message.message = textStatus + (errorThrown ? ': ' + errorThrown : '');
		    			if (jqXHR.responseText.indexOf('body') > 0) {
		    				message.detail = jqXHR.responseText;
		    			}
		    			message.level = 'error';
		    			$.metalisxMessages(message);
		            	$('#' + id + '_processing', $this.parent()).hide();
		        	}
            	}
    		});
		};

		function dataTableContextToPageContext(data, filter) {
        	var pageContext = {};
        	var orderBy = {};
        	var limit = {};

        	limit.start = data.start
        	limit.count = data.length;

        	var columns = data.columns;
        	var order = data.order;
        	for (var i=0; i<order.length; i++) {
        		var column = order[i].column;
        		if (columns[column].name != null && columns[column].name != '') {
                	orderBy.columnName = columns[column].name; 
        		} else {
        			orderBy.columnName = column;
        		}
               	orderBy.columnDirection = order[i].dir
        	}

       		pageContext.orderBy = orderBy;
       		pageContext.limit = limit;
        	if (filter) {
        		pageContext.filter = filter;
        	}
        	return pageContext;
		}
		
		function pageToDataTableResult(page) {
			var dataTableResult = {
					data: page.items,
					recordsFiltered: page.totalNumberOfRows,
					recordsTotal: page.totalNumberOfRows,
				};
			if (page.columnNames) {
				dataTableResult.sColumns = page.columnNames.join(',');
			}
			return dataTableResult;
		}
		
		this.getDataTable = function() {
			return dataTable;
		}

		if (!settings.dataTableSettings.ajax) {
			alert('Property dataTableSettings.ajax must be set in the options parameter.');
		}
		
		var messagesContainer = $('#' + settings.messagesContainerId);
		if (messagesContainer.size() == 1) {
			messagesContainer.hide();
			messagesContainer.empty();
		} else {
			alert('Missing html container to place alerts. Please add a html container(div) in the body with the following id ' + settings.messagesContainerId);
		}

		var $this = this;
		var id = $this.attr('id');
		
		dataTable = $this.dataTable(settings.dataTableSettings);
		if (settings.onCreation) {
			settings.onCreation(dataTable);
		}
		
		return this;
	};
	
})(jQuery);
