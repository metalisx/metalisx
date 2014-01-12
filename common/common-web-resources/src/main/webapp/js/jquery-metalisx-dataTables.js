/**
 * jQuery plugin for a default configured DataTables styled with bootstrap.
 * 
 * The method metalisxDataTable excepts the following parameters in the same
 * order:
 *  - filter
 *  - options
 * 
 * The filter parameter can contain an object. This object is added to the
 * pageContext and posted to the server.
 * 
 * The posted object structure is:
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
 * The returned object structure is:
 * {
 * 		items: <list of objects>,
 * 		totalNumberOfRows: <total number of rows>
 * }
 * 
 * The options parameter provides the plugin with some settings. One of
 * the properties in options is dataTableSettings where you can set the
 * defaults for the DataTable plugin.
 *
 * The only required setting is the URL from which the data is retrieved. Set 
 * the URL in the options object parameter as dataTableSettings.sAjaxSource. 
 * Example: {
 * 				dataTableSettings: {
 * 										"sAjaxSource": myUrl
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
 * clicks on a row a new row is added below this row and the renderDetail function 
 * is called to populate the row.
 * 
 * The options parameter has a property onsuccess. It is default null
 * and can contain a method without parameters. It will run after the
 * first retrieval of data from the server.
 */
(function($){

	$.fn.metalisxDataTable = function(filter, options) {
		var runOnsuccess = true;
		var settings = $.extend(true, {
			messagesContainerId: 'messagesContainer',
			onsuccess: null,
			onsuccessRow: null,
			renderDetail: null, 
			dataTableSettings: {
				"bAutoWidth": false,
				"bDestroy": true,
				"bProcessing": true,
				"bServerSide": true,
				"bFilter": false,
				"bSortClasses": false,
				"sPaginationType": "full_numbers", 
				"sPaginationType": "bootstrap", // Bootstrap
				"fnCreatedRow" : function(nRow, aData, iDataIndex) {
					$('a', $(nRow)).click(function(event) {
						event.stopImmediatePropagation();
					});
					if (settings.renderDetail) {
						$(nRow).click(function() {
							if (dataTable.fnIsOpen(this) ) {
								dataTable.fnClose(this);
							} else {
								var nRows = dataTable.fnGetNodes();
								for (var i=0; i < nRows.length; i++) {
									if (dataTable.fnIsOpen(dataTable.fnGetNodes(i))) {
										dataTable.fnClose(dataTable.fnGetNodes(i));
									}
								}
								var $detailContainer = $('<div></div>');
								dataTable.fnOpen(this, $detailContainer, "info_row");
								settings.renderDetail($detailContainer, nRow, aData, iDataIndex);
							}
						});
					}
					if (settings.onsuccessRow) {
						settings.onsuccessRow(nRow, aData, iDataIndex);
					}
				},
		        "fnServerData": function ( sSource, aoData, fnCallback, oSettings ) {
		        	
		        	// An interceptor function will show errors gracefully.
	    			messagesContainer.hide();
	    			messagesContainer.empty();

		        	var innerFnCallback = function(result) {
		        		if (result.messages) {
		        			$.metalisxMessages(result.messages, {messagesContainerId: settings.messagesContainerId});
							$('#' + id + '_processing', $this.parent()).hide();
						} else {
							var dataTableResult = pageToDataTableResult(result);
							fnCallback(dataTableResult);
							if (settings.onsuccess && runOnsuccess) {
								runOnsuccess = false;
								settings.onsuccess();
							}
						}
		        	};

		        	var pageContext = dataTableContextToPageContext(aoData, filter);
		        	
		        	oSettings.jqXHR = $.ajax({
							"type": "POST",
							"contentType": "application/json",
							"dataType": "json",
							"url": sSource,
							"data": JSON.stringify(pageContext),
							"success": innerFnCallback}
		            ).fail(function(jqXHR, textStatus, errorThrown) {
		    			var message = {};
		    			message.id = settings.messagesContainerId;
		    			message.message = textStatus + (errorThrown ? ': ' + errorThrown : '');
		    			if (jqXHR.responseText.indexOf('body') > 0) {
		    				message.detail = jqXHR.responseText;
		    			}
		    			message.level = 'error';
		    			$.metalisxMessages(message);
		            	$('#' + id + '_processing', $this.parent()).hide();
		    		});
		        }
			}
		}, options || {});
		
		function dataTableContextToPageContext(aoData, filter) {
        	var pageContext = {};
        	var orderBy = {};
        	var limit = {};

        	// Find if sColumns are set, if so then use the name in the sorting instead of the index
			var columnNames = null;
        	for (var j=0; j < aoData.length; j++) {
        		if (aoData[j].name == 'sColumns') {
        			sColumns = aoData[j].value;
        			columnNames = sColumns.split(',');
        		}
        	}

        	for (var i=0; i < aoData.length; i++) {
        		if (aoData[i].name == 'iDisplayStart') {
        			limit.start = aoData[i].value;
        		} else if (aoData[i].name == 'iDisplayLength') {
        			limit.count = aoData[i].value;
        		} else if (aoData[i].name == 'iSortCol_0') {
            		if (columnNames) { // translate the iSortCol index to the name in sColumns
            			orderBy.columnName = columnNames[aoData[i].value];
            		} else {
            			orderBy.columnName = aoData[i].value;
            		}
        		} else if (aoData[i].name == 'sSortDir_0') {
        			orderBy.columnDirection = aoData[i].value;
        		}
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
					aaData: page.items,
					iTotalDisplayRecords: page.totalNumberOfRows,
					iTotalRecords: page.totalNumberOfRows,
				};
			if (page.columnNames) {
				dataTableResult.sColumns = page.columnNames.join(',');
			}
			return dataTableResult;
		}

		if (!settings.dataTableSettings.sAjaxSource) {
			alert('Property dataTableSettings.sAjaxSource must be set in the options parameter.');
		}
		
		var messagesContainer = $('#' + settings.messagesContainerId);
		if (messagesContainer.size() == 1) {
			messagesContainer.hide();
			messagesContainer.empty();
		} else {
			alert('Missing html container to place alerts. Please specify a div with id ' + settings.messagesContainerId);
		}

		var $this = this;
		var id = $this.attr('id');
		
		var dataTable = $this.dataTable(settings.dataTableSettings);
		
		// We override the fnClose and fnDraw method on the dataTable.
		// This is done so the child elements in the open details tr are 
		// removed to trigger the jQuery remove events attached on these 
		// elements. This way you have the ability to remove the TinyMCE 
		// editors if it was bind to an element.
		// It will run on the entire table.
		var dataTableFnClose = dataTable.fnClose;
		dataTable.fnClose = function(nTr) {
			$('.info_row', $this).children().remove();
			return dataTableFnClose.call(this, nTr);
		};
		var dataTableFnDraw = dataTable.fnDraw;
		dataTable.fnDraw = function() {
			$('.info_row', $this).children().remove();
			return dataTableFnDraw.call(this);
		};
		
		return dataTable;
	};
	
})(jQuery);
