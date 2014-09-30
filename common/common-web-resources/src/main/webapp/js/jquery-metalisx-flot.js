/**
 * A convenience jQuery plugin for the flot plugin.
 * 
 * Flot API doc: http://flot.googlecode.com/svn/trunk/API.txt
 * 
 * The plugin should run against a div:
 *  $('#chartContainer').metalisxBarChart(myUrl);
 *  $('#chartContainer').metalisxLineChart(myUrl);
 *  $('#chartContainer').metalisxPieChart(myUrl);
 *  $('#chartContainer').metalisxTimeLineChart(myUrl);
 * 
 * The pugin returns the plot object created by Flot, instead of the this object.
 * 
 * This plugin uses the Ajax request with type POST, content type application/json and
 * dataType JSON. This is the most robust and convenient way to handle the AJAX data on the 
 * server side. As the server side can use a framework to automatically transform the 
 * received JSON object. 
 * If the type is POST and the content type is 'application/json' the data will be converted with
 * the JSON.stringify, this requires an JSON implementation to be present.
 * 
 * The url should return a JSON object of the form:
 *  { "items" : [{"y":3,"x":"batman"},{"y":1,"x":"spider-man"}]}
 * 
 * Required CCS classes can be found in the section "Flot"
 * in the stylesheet file common.css. 
 *
 * Stefan Oude Nijhuis
 */

(function($){

	$.fn.metalisxSaveChart = function(options) {

		var settings = $.extend(true, {
			type: 'PNG' // valid values are PNG, BMP, JPEG
		}, options || {});
			
		var $this = this;

		function saveFlotGraphAsPNG() {
			var bRes = null;
			var canvas = $('canvas:first', $this).get(0);
			if (settings.type == 'PNG') {
				bRes = Canvas2Image.saveAsPNG(canvas);
			} else if (settings.type == 'JPEG') {
				bRes = Canvas2Image.saveAsJPEG(canvas);
			} else if (settings.type == 'BMP') {
				bRes = Canvas2Image.saveAsBMP(canvas);
			}
			if (!bRes) {
				alert("Sorry, this browser is not capable of saving " + settings.type + " files!");
				return false;
			}
        }
        		
   		saveFlotGraphAsPNG();

		return this;
		
	};
	
	$.fn.metalisxBarChart = function(url, options) {
		var settings = null;
		settings = $.extend(true, {
			getX: function (index, item) { return item['x']; },
			getY: function (index, item) { return item['y']; },
		    data: function(items) {
		    	var data = [];
				$.each(items, function(index, item) {
					data.push([index, settings.getY(index, item)]);
				});
				var dataSerie = {};
				if (settings.label != null) {
					dataSerie['label'] = settings.label;
				}
				dataSerie['data'] = data;
				return [dataSerie];
		    },
	    	xaxisTicks: function(items) {
		    	var xaxisTicks = [];
				$.each(items, function(index, item) {
					xaxisTicks.push([index, settings.getX(index, item)]);
				});
				return xaxisTicks;
	    	},
			flotSettings: {
				series: {
					bars: { show: true, barWidth: 0.6, align: 'center' }
				},
			    xaxis: {
			    },
			    yaxis: {
					//tickSize: 1,
					tickDecimals: 0
			    }
			}
		}, options || {});
		
		this.metalisxChart(url, settings);
	};

	/*
	 * Convenience plugin to support dates on the x-axis with a predefined configuration.
	 * It delegates to the metalisxLineChart plugin and all it's options
	 * are valid options for this plugin.
	 * When the provided data (returned by the getX method) is not a Date it will 
	 * check if the data is a Number or a String. When this is the case, it will 
	 * try to parse the data to a Date. This process will also be executed on the
	 * xaxix min and max.
	 * Example format which is accepted, hopefully by most Data.parse
	 * implementations, is: yyyy-MM-ddTHH:mm:ss.fff
	 * You can also convert the data to a date before passing it to flot. For
	 * example with: new Date(Date.parse(myDateString, 'yyyy-MM-ddTHH:mm:ss.fff'))
	 */  
	$.fn.metalisxTimeLineChart = function(url, options) {

		var settings = $.extend(true, {
			getX: function (index, item) { return item['x']; },
			flotSettings: {
				grid: { hoverable: true },
				xaxis: {
			    	mode: "time",
					timeformat: "%d-%m-%y %H:%M:%S",
					timezone: 'browser',
					minTickSize: [1, "second"]
			    }
			},
			flotTooltipId: 'flotTooltip'
		}, options || {});

		var $this = $(this);
		
		function getDate(value) {
			if (value == null || value == '') return null;
			if (value instanceof Date) return value;
			var date = null;
			if (typeof value === 'number') {
				date = new Date(value);
			} else if (typeof value === 'string') { 
				// Expecting a ISO date as string, example 2012-09-15-2012T13:42:33.543
				// Because IE 8 and lower can not parse a ISO date string we need to split the 
				// string into parts to construct the date. Note: month in a Date starts at 0. 
				// No safety checks as the date should be provided correctly.
				var dateTimeParts = value.split("T");
				var dateParts = dateTimeParts[0].split('-');
				var timeMillisecondParts = dateTimeParts[1].split('.');
				var timeParts = timeMillisecondParts[0].split(':');
				date = new Date(dateParts[0], dateParts[1]-1, dateParts[2], timeParts[0], timeParts[1], timeParts[2], timeMillisecondParts[1]);
			}
			if (!(date instanceof Date)) {
				console.log('metalisxTimeLineChart() -> Not an instance of Date and not parsable to a date ' + getX(index, item));
			}
			return date;
		}
		
		function fixDates() {
			var getX = settings.getX;
			settings.getX = function(index, item) {
				return getDate(getX(index, item));
			};
			settings.flotSettings.xaxis.min = getDate(settings.flotSettings.xaxis.min);
			settings.flotSettings.xaxis.max = getDate(settings.flotSettings.xaxis.max);
			
			if (settings.getXaxisMin) {
				var getXaxisMin = settings.getXaxisMin;
				settings.getXaxisMin = function(data) {
					return getDate(getXaxisMin(data));
				};
			}
			if (settings.getXaxisMax) {
				var getXaxisMax = settings.getXaxisMax;
				settings.getXaxisMax = function(data) {
					return getDate(getXaxisMax(data));
				};
			}
		}

		function renderTooltip() {

			function showTooltip(x, y, content) {
				var $tooltip = $('<div id="' + settings.flotTooltipId + '" class="flot-tooltip"></div>').
					css({top: y + 5, left: x + 5}).append(content);
				$tooltip.appendTo('body').fadeIn(200);
			}

			var previousPoint = null;
		    $this.off("plothover");
		    $this.on("plothover", function (event, pos, item) {
		    	if (item) {
		    		if (previousPoint != item.dataIndex) {
		    			previousPoint = item.dataIndex;
		    			$('#' + settings.flotTooltipId).remove();
		    			var dataItem = item.series.data[item.dataIndex];
		                $content = $('<div style="padding:5px"></div>').
		                	append('<div>x = ' + $.metalisxUtils.dateToDisplayDate(dataItem[0]) + '</div').
		                	append('<div>y = ' + dataItem[1] + '</div>');
		    			showTooltip(item.pageX, item.pageY, $content);
		    		}
		    	} else {
		    		$('#' + settings.flotTooltipId).remove();
		    		previousPoint = null;            
		    	}
		    });
		}
		
		fixDates();
	    if (settings.flotSettings.grid.hoverable) {
	    	renderTooltip();
	    }
	    
	    this.metalisxLineChart(url, settings);
	};
	
	$.fn.metalisxLineChart = function(url, options) {
		var settings = null;
		settings = $.extend(true, {
			getX: function (index, item) { return item['x']; },
			getY: function (index, item) { return item['y']; },
		    data: function(items) {
		    	var data = [];
				$.each(items, function(index, item) {
					data.push([settings.getX(index, item), settings.getY(index, item)]);
				});
				var dataSerie = {};
				if (settings.label != null) {
					dataSerie['label'] = settings.label;
				}
				dataSerie['data'] = data;
				return [dataSerie];
		    },
			flotSettings: {
				series: {
					lines: { show: true },
					points: { show: true, radius: 4 },
					shadowSize: 0
			    },
			    xaxis: {
			    },
			    yaxis: {
					//tickSize: 1,
					tickDecimals: 0
			    }
			}
		}, options || {});

		this.metalisxChart(url, settings);
	};
	
	$.fn.metalisxPieChart = function(url, options) {
		var settings = null;
		settings = $.extend(true, {
			getX: function (index, item) { return item['x']; },
			getY: function (index, item) { return item['y']; },
		    data: function(items) {
		    	var data = [];
				$.each(items, function(index, item) {
					data.push({'label': settings.getX(index, item), 'data': settings.getY(index, item)});
				});
				return data;
		    },
			flotSettings: {
				legend: {
							show: true,
					        labelFormatter: function(label, series) {
					            var percent = '';
					            if (settings.flotSettings.legend.show) {
					            	percent = ' (' + Math.round(series.percent) + '%)';
					            }
					            //var data = series.data[0][1];
					            return label + percent;
					        }
				},
				series: {
					pie: { show: true, labelWidth: 30, fontSize: 10, autoScale: true, fillOpacity: 1 }
			    }
			}
		}, options || {});

		this.metalisxChart(url, settings);
	};
	
	$.fn.metalisxChart = function(url, options) {

		var settings = $.extend(true, {
			requestType: 'POST',
			requestContentType: "application/json",
			requestData: null, 	// If url is an URL endpoint, requestType is POST and requestContentType is application/json
								// then the object is posted as a JSON object otherwise this object
								// is attached to the url parameter
			getX: function (index, item) { return item['x']; },
			getY: function (index, item) { return item['y']; },
			getXaxisMin: null,
			getXaxisMax: null,
			data: function(items) { return []; },
			xaxisTicks: null,
			flotSettings: {
				xaxis: {
				},
				yaxis: {
				},
				legend: {show: true},
				series: {},
				grid: { 
					show: true,
					hoverable: true, 
					clickable: true , 
					color: "#999",
					canvasText: {show: true, font: "sans 9px"}
				}
			},
			timeout: null,
			showSaveButton: true
		}, options || {});

		var $this = this;
		var plot = null;
		
		if (!url) {
			alert('Parameter url is not set.');
		}
		
		function renderSaveButton() {
			var $buttonGroup = $('<div class="btn-group flot-buttonGroup"></div>');
		    var $saveButton = $('<button class="btn">Save</button>').
		    	click(function (e) {
			    	e.preventDefault();
			    	$this.metalisxSaveChart();
			    	return false;
			    });
		    $buttonGroup.append($saveButton);
		    var $dropdownButton = $('<button class="btn btn-default dropdown-toggle" data-toggle="dropdown"><span class="caret"></span></button>');
		    $buttonGroup.append($dropdownButton);
		    var $dropdownGroup = $('<ul class="dropdown-menu"/>');
		    var $savePNG = $('<li><a href="#">Save as PNG</a></li>').
		    	click(function (e) {
		    		e.preventDefault();
		    		$this.metalisxSaveChart({type: 'PNG'});
		    	});
		    $dropdownGroup.append($savePNG);
		    var $saveJPEG = $('<li><a href="#">Save as JPEG</a></li>').
		    	click(function (e) {
		    		e.preventDefault();
		    		$this.metalisxSaveChart({type: 'JPEG'});
		    	});
		    $dropdownGroup.append($saveJPEG);
		    var $saveBMP = $('<li><a href="#">Save as BMP</a></li>').
		    	click(function (e) {
		    		e.preventDefault();
		    		$this.metalisxSaveChart({type: 'BMP'});
		    	});
		    $dropdownGroup.append($saveBMP);
		    $buttonGroup.append($dropdownGroup);
		    $this.append($buttonGroup);
		}
		
		function render(result) {
			$this.empty();
			var items = null;
			if (result) {
				if (result.items) {
					items = result.items;
				} else {
					items = result;
				}
			}
			if (items != null) {
				if (settings.xaxisTicks != null) {
					settings.flotSettings.xaxis.ticks = settings.xaxisTicks(items);
				}
				if (settings.getXaxisMin) {
					settings.flotSettings.xaxis.min = settings.getXaxisMin(result);
				}
				if (settings.getXaxisMax) {
					settings.flotSettings.xaxis.max = settings.getXaxisMax(result);
				}
			    plot = $.plot($this, settings.data(items), settings.flotSettings);
			    if (settings.showSaveButton) {
			    	renderSaveButton();
			    }
				if (settings.onsuccess) {
					settings.onsuccess(plot, result);
				}
				// create a refreshData method on the plot object to handle a range change
			    plot.refreshData = function(data) {
			    	plot.setData(settings.data(data));
			    	plot.setupGrid();
			    	plot.draw();
			    	plot.clearSelection();
			    };
			}
		}
		
	    function fetchData(url) {
	    	if (url === 'String') {
		    	var data;
		    	if (settings.requestType == 'POST' && settings.requestContentType == 'application/json') {
		    		data = JSON.stringify(settings.requestData);
		    	} else {
		    		data = settings.requestData;
		    	}
		    	$.ajax({
		            url: url,
		        	type: settings.requestType,
		        	contentType: settings.requestContentType,
		            data: data,
		            dataType: 'json',
		            success: render
		        });
			    if (settings.timeout != null && settings.timeout > 0) {
			    	setTimeout(function() {	fetchData(url); }, settings.timeout);
			    }
	    	} else {
	    		render(url);
	    	}
        }

	    fetchData(url);

	};
	
})(jQuery);
