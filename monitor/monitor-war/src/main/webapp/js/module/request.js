function RequestController($scope, $compile, $http, $location, $window, $routeParams, crudService, 
		applicationContext) {

	var requestUrl = applicationContext.contextPath + '/rest/requests/list-item/';
	var resendEndpoint = applicationContext.contextPath + '/rest/requests/request/resend/';

	$scope.entity = null;
	
	// Init
	
	function init() {
		$scope.initEntity();
	}
	
	// Entity
	
	$scope.initEntity = function() {
		var id = $routeParams.id;
		$scope.getEntity(id);
	}

	$scope.getEntity = function(id) {
    	$.metalisxDataProvider.get(requestUrl + id, null, {onsuccess: function(result) {
    		$scope.entity = result.item;
    		$scope.$apply();
    	}});
    }

	// Actions
	
	$scope.resend = function($event) {
		$event.stopPropagation();
		$event.preventDefault();
		crudService.get(resendEndpoint + $scope.entity.id, null);
	};
	
	init();
}

application.directive('ngcRequestDynatree', function(applicationContext) {
	return {
		restrict: 'A',
		require: 'ngModel',
		link: function(scope, element, attrs, ngModel) {
			
			var renderDynatree = function($element, entity) {

				var urls = $.extend(true, {
					requestsPageUrl: applicationContext.contextPath + '/page/requests.html?requestId=',
					requestContentUrl: applicationContext.contextPath + '/rest/requests/request/content/',
					requestContentAsTextUrl: applicationContext.contextPath + '/rest/requests/request/content-as-text/',
					requestContentPrettyPrintUrl: applicationContext.contextPath + '/rest/requests/request/content-pretty-print/',
					responseContentUrl: applicationContext.contextPath + '/rest/requests/response/content/',
					responseContentAsTextUrl: applicationContext.contextPath + '/rest/requests/response/content-as-text/',
					responseContentPrettyPrintUrl: applicationContext.contextPath + '/rest/requests/response/content-pretty-print/',
					partContentUrl: applicationContext.contextPath + '/rest/requests/part/content/',
					partContentAsTextUrl: applicationContext.contextPath + '/rest/requests/part/content-as-text/',
					partContentPrettyPrintUrl: applicationContext.contextPath + '/rest/requests/part/content-pretty-print/',
					requestCertificateInfoUrl: applicationContext.contextPath + '/rest/requests/certificate/info/',
					requestCertificateDownloadUrl: applicationContext.contextPath + '/rest/requests/certificate/download/',
					requestLogsUrl: applicationContext.contextPath + '/rest/logs/request/tree/'
				});

				var $this = $element;

				function value(value) {
					return (value != null ? value : '');
				}

				function renderTree() {
					var $tree = $('<div class="tree"></div>');
					$this.append($tree);
					$tree.dynatree({
						onClick: function(node) {
							if (node.data.href) {
								window.location.href = node.data.href;
							}
						},
						onCreate: function(node, nodeSpan) {
							if (node.data.monitorNodeType == 'requestCertificateInfo') {
								$(nodeSpan).click(function(e){
							    	if ($('.content',$(this).parent()).size() > 0) {
							    		$('a',$(this)).html('Info >>');
							    		$('.content',$(this).parent()).remove();
							    	} else {
										var $this = $(this);
										$.ajax({
											url: urls.requestCertificateInfoUrl + node.data.certificateId,
											success: function(data){
									    		$('a',$this).html('Info <<');
												if (!data) {
													data = '';
												}
												data = $.metalisxUtils.escapeHtml(data);
									    		var $pre = $('<pre/>').html(data);
									    		var $div = $('<div class="content certificate"/>').append($pre);
										    	$this.parent().append($div);
										    }
										});
							    	}
								});
							} else if (node.data.monitorNodeType == 'requestCertificateDownload') {
								$('a', nodeSpan).attr('href','http://www.google.com');
								$(nodeSpan).click(function(e){
									window.location.href=urls.requestCertificateDownloadUrl + node.data.certificateId;
								});
							} else if (node.data.monitorNodeType == 'requestContent' ||
								node.data.monitorNodeType == 'responseContent' ||
								node.data.monitorNodeType == 'partContent') {
					    		var url = '';
					    		var additionalClass = '';
					    		var monitorPrettyPrint = node.data.monitorPrettyPrint;
					    		if (node.data.monitorIsTextContext) {
									if (node.data.monitorNodeType == 'requestContent') {
										if (monitorPrettyPrint) {
											url = urls.requestContentPrettyPrintUrl + node.data.requestId;
										} else {
											url = urls.requestContentAsTextUrl + node.data.requestId;
										}
									} else if (node.data.monitorNodeType == 'responseContent') {
										if (monitorPrettyPrint) {
											url = urls.responseContentPrettyPrintUrl + node.data.responseId;
										} else {
											url = urls.responseContentAsTextUrl + node.data.responseId;
										}
									} else if (node.data.monitorNodeType == 'partContent') {
										if (monitorPrettyPrint) {
											url = urls.partContentPrettyPrintUrl + node.data.partId;
										} else {
											url = urls.partContentAsTextUrl + node.data.partId;
										}
									}
					    		} else {
									if (node.data.monitorNodeType == 'requestContent') {
										url = urls.requestContentUrl + node.data.requestId;
									} else if (node.data.monitorNodeType == 'responseContent') {
										url = urls.responseContentUrl + node.data.responseId;
									} else if (node.data.monitorNodeType == 'partContent') {
										url = urls.partContentUrl + node.data.partId;
									}
					    		}
								if (node.data.monitorNodeType == 'partContent') {
									additionalClass=' partContent';
								}
							    $(nodeSpan).click(function(e){
							    	if ($('.content',$(this).parent()).size() > 0) {
							    		$('a',$(this)).html((monitorPrettyPrint ? 'Content Pretty Print >>' : 'Content >>'));
							    		$('.content',$(this).parent()).remove();
							    	} else {
										var $this = $(this);
										if (node.data.monitorIsTextContext == true) {
											$.ajax({
												url: url,
												success: function(data){
													if (!data) {
														data = '';
													}
													data = $.metalisxUtils.escapeHtml(data);
										    		$('a',$this).html((monitorPrettyPrint ? 'Content Pretty Print <<' : 'Content <<'));
										    		var $pre = $('<pre/>');
										    		if (monitorPrettyPrint) {
										    			$pre.addClass("prettyprint");
										    			data = prettyPrintOne(data);
										    		}
									    			$pre.html(data);
										    		var $div = $('<div class="content' + additionalClass + '"/>').append($pre);
											    	$this.parent().append($div);
											    }
											});
										} else {
								    		$('a',$(this)).html((monitorPrettyPrint ? 'Content Pretty Print <<' : 'Content <<'));
											var iframe = '<iframe src="' + url + '"/>';
									    	$(this).parent().append('<div class="content' + additionalClass + '">' + iframe + '</div>');
										}
							    	}
							    });
							}
						},
						onLazyRead: function(node) {
							if (node.data.monitorNodeType == 'loggingContent') {
								$.ajax({
									url: urls.requestLogsUrl + node.data.requestId,
									success: function(data){
										node.setLazyNodeStatus(DTNodeStatus_Ok);
										if (data && data.item) {
											toNodesChildren(node, data.item.children);
										}
									}
								});
							}
						}
					});
					return $tree;
				}
				
				function toNodesChildren(node, children) {
					$.each(children, function(index, item) {
						var title = ('<div>'  + item.monitorLog.duration + ' ms : ' + item.monitorLog.message + '</div>');
						if (item.children && item.children.length > 0) {
							var monitorLogNode = node.addChild({title: title, key: item.monitorLog.id + 'log', isFolder: true});
							toNodesChildren(monitorLogNode, item.children);
						} else {
							node.addChild({title: title, key: item.monitorLog.id + 'log'});
						}
					});
				}
				
				function getRootNode() {
					var $tree = $(".tree", $this);
					if ($tree.size() == 0) {
						$tree = renderTree();
					}
					var rootNode = $tree.dynatree("getRoot");
					return rootNode;
				}
				
				function renderEntity(entity) {
					if (entity) {
						renderRequest(entity);
					}
				}
				
				function renderRequest(request) {
					rootNode = getRootNode();
					if (rootNode) {
						renderRequestNode(rootNode, request);
					}
				}

				function renderRequestNode(requestnode, request) {
					
					requestnode.addChild({title: '<div>' + 'Url: ' + request.url + "</div>", key: request.id + 'url'});
					requestnode.addChild({title: 'Thread: ' + request.thread, key: request.id + 'thread'});
					requestnode.addChild({title: 'Request id: ' + request.requestId, key: request.id + 'requestId'});
					if (request.parentRequestId) {
						requestnode.addChild({title: 'Parent request id: ' + request.parentRequestId, key: request.id + 'parentRequestId',
							href: urls.requestsPageUrl + request.parentRequestId});
					}
					if (request.organization != null) {
						requestnode.addChild({title: 'Organization: ' + request.organization, key: request.id + 'organization'});
					}
					if (request.username != null) {
						requestnode.addChild({title: 'Username: ' + request.username, key: request.id + 'username'});
					}
					requestnode.addChild({title: 'Start time: ' + request.startTime, key: request.id + 'startTime'});
					requestnode.addChild({title: 'End time: ' + request.endTime, key: request.id + 'endTime'});
					requestnode.addChild({title: 'Duration (in ms): ' + request.duration, key: request.id + 'duration'});
					
					// Request
					var requestNode = requestnode.addChild({title: 'Request', key: request.id + 'request', isFolder: true});
					if (request.method != null) {
						requestNode.addChild({title: 'Request Method: ' + value(request.method), key: request.id + 'requestMethod'});
					}
					if (request.characterEncoding != null) {
						requestNode.addChild({title: 'Character Encoding: ' + value(request.characterEncoding), key: request.id + 'requestCharacterEncoding'});
					}
					if (request.contentType != null) {
						requestNode.addChild({title: 'Content Type: ' + value(request.contentType), key: request.id + 'requestContentType'});
					}
					if (request.locale != null) {
						requestNode.addChild({title: 'Locale: ' + request.locale, key: request.id + 'requestLocale'});
					}
					if (request.secure != null) {
						requestNode.addChild({title: 'Secure: ' + value(request.secure), key: request.id + 'requestSecure'});
					}
					if (request.authType != null) {
						requestNode.addChild({title: 'Auth Type: ' + value(request.authType), key: request.id + 'requestAuthType'});
					}
					if (request.remoteUser != null) {
						requestNode.addChild({title: 'Remote User: ' + value(request.remoteUser), key: request.id + 'requestRemoteUser'});
					}
					if (request.userPrincipal != null) {
						requestNode.addChild({title: 'User Principal: ' + value(request.userPrincipal), key: request.id + 'requestUserPprincipal'});
					}
					requestNode.addChild({title: 'Content Length: ' + value(request.contentLength), key: request.id + 'requestContentLength'});
					requestNode.addChild({title: 'Actual Content Length: ' + value(request.actualContentLength), key: request.id + 'requestActualContentLength'});
					if (request.actualContentLength > 0) {
						requestNode.addChild({title: 'Content >>', 
							monitorIsTextContext: request.textContent, 
							monitorPrettyPrint: false,
							monitorContentType: request.contentType,
							monitorNodeType: 'requestContent', isLazy: false, isFolder: false,
							requestId: request.id, key: request.id + 'requestContent'});
						if (request.prettyPrint) {
							requestNode.addChild({title: 'Content Pretty Print >>', 
								monitorIsTextContext: request.textContent, 
								monitorPrettyPrint: true,
								monitorNodeType: 'requestContent', isLazy: false, isFolder: false,
								requestId: request.id, key: request.id + 'requestContent'});
						}
					}
					// URL
					var urlNode = requestNode.addChild({title: 'URL parts', key: request.id + 'requestUrl', isFolder: true});
					if (request.scheme != null) {
						urlNode.addChild({title: 'Scheme: ' + value(request.scheme), key: request.id + 'requestScheme'});
					}
					if (request.serverName != null) {
						urlNode.addChild({title: 'Server Name: ' + value(request.serverName), key: request.id + 'requestServerName'});
					}
					if (request.serverPort != null) {
						urlNode.addChild({title: 'Server Port: ' + value(request.serverPort), key: request.id + 'requestServerPort'});
					}
					if (request.contextPath != null) {
						urlNode.addChild({title: 'Context Path: ' + value(request.contextPath), key: request.id + 'requestContextPath'});
					}
					if (request.servletPath != null) {
						urlNode.addChild({title: 'Servlet Path: ' + value(request.servletPath), key: request.id + 'requestServletPath'});
					}
					if (request.pathInfo != null) {
						urlNode.addChild({title: 'Path Info: ' + value(request.pathInfo), key: request.id + 'requestPathInfo'});
					}
					if (request.queryString != null) {
						urlNode.addChild({title: 'Query String: ' + value(request.queryString), key: request.id + 'requestQueryString'});
					}
					if (request.pathTranslated != null) {
						urlNode.addChild({title: 'Path translated: ' + value(request.pathTranslated), key: request.id + 'requestPathTranslated'});
					}
					// Security
					if (request.security && !$.isEmptyObject(request.security)) {
						var requestSecurityNode = requestNode.addChild({title: 'Security', key: request.id + 'requestSecurity', isFolder: true});
						for (var name in request.security) {
							if (name != 'id') {
								requestSecurityNode.addChild({title: name + ': ' + value(request.security[name]), key: request.id + 'requestSecurity' + name});
							}
						}
					}
					// Certificates
					if (request.certificates && request.certificates.length > 0) {
						var requestCertificatesNode = requestNode.addChild({title: 'Certificates', key: request.id + 'requestCertificates', isFolder: true});
						$.each(request.certificates, function(index, certificate) {
							var requestCertificateNode = requestCertificatesNode.addChild(
									{title: 'Certificate: ' + value(certificate.subjectDn), key: request.id + 'requestCertificate' + certificate.name, isFolder: true});
							requestCertificateNode.addChild({title: 'Valid: ' + certificate.valid, key: request.id + 'requestCertificate' + certificate.id + "valid"});
							requestCertificateNode.addChild({title: 'Subject DN: ' + certificate.subjectDn, key: request.id + 'requestCertificate' + certificate.id + "subjectDn"});
							requestCertificateNode.addChild({title: 'Issuer DN: ' + certificate.issuerDn, key: request.id + 'requestCertificate' + certificate.id + "issuerDn"});
							requestCertificateNode.addChild({title: 'Type: ' + certificate.type, key: request.id + 'requestCertificate' + certificate.id + "type"});
							requestCertificateNode.addChild({title: 'Info >>', key: request.id + 'requestCertificate' + certificate.id + 'info',
								monitorNodeType: 'requestCertificateInfo',
								certificateId: certificate.id});
							requestCertificateNode.addChild({title: 'Download >>', key: request.id + 'requestCertificate' + certificate.id + 'donwload',
								monitorNodeType: 'requestCertificateDownload',
								certificateId: certificate.id});
						});
					}
					// Headers
					if (request.headers && request.headers.length > 0) {
						var requestHeadersNode = requestNode.addChild({title: 'Headers', key: request.id + 'requestHeaders', isFolder: true});
						$.each(request.headers, function(index, header) {
							requestHeadersNode.addChild({title: header.name + ': ' + value(header.value), key: request.id + 'requestHeader' + header.id});
						});
					}
					// Cookies
					if (request.cookies && request.cookies.length > 0) {
						var requestCookiesNode = requestNode.addChild({title: 'Cookies', key: request.id + 'requestCookies', isFolder: true});
						$.each(request.cookies, function(index, cookie) {
							var requestCookieNode = requestCookiesNode.addChild(
									{title: 'Cookie Name: ' + value(cookie.name), key: request.id + 'requestCookie' + cookie.name, isFolder: true});
							for (var name in cookie) {
								if (name != 'id') {
									requestCookieNode.addChild({title: name + ': ' + value(cookie[name]), key: request.id + 'requestCookie' + name});
								}
							}
						});
					}
					// Session
					if (request.session && !$.isEmptyObject(request.session)) {
						var session = request.session;
						var requestSessionNode = requestNode.addChild({title: 'Session Id: ' + session.requestedSessionId,
							key: request.id + 'requestSessionId', isFolder: true});
						for (var name in session) {
							if (name != 'id') {
								requestSessionNode.addChild({title: name + ': ' + value(session[name]), key: request.id + 'requestSession' + session[name]});
							}
						}
					}
					// Locales
					if (request.locales && request.locales.length > 0) {
						var requestLocalesNode = requestNode.addChild({title: 'Locales', key: request.id + 'requestLocales', isFolder: true});
						for (var i=0; i<request.locales.length; i++) {
							requestLocalesNode.addChild({title: value(request.locales[i].name), key: request.id + 'requestLocales' + request.locales[i].id});
						}
					}
					// Multipart
					if (request.parts && request.contentType && request.contentType.toLowerCase().substring(0, 10) == 'multipart/') {
						var requestPartsNode = requestNode.addChild({title: 'Parts', key: request.id + 'requestParts', isFolder: true});
						for (var i=0; i<request.parts.length; i++) {
							var part = request.parts[i];
							var requestPartNode = requestPartsNode.addChild({title: 'Part', key: request.id + 'requestPart' + part.id, isFolder: true});
							requestPartNode.addChild({title: 'Name: ' + value(part.name), key: request.id + 'requestPart' + part.id + 'name'});
							if (part.filename != null) {
								requestPartNode.addChild({title: 'File Name: ' + value(part.filename), key: request.id + 'requestPart' + part.id + 'filename'});
								requestPartNode.addChild({title: 'Content Type: ' + value(part.contentType), key: request.id + 'requestPart' + part.id + 'contentType'});
								requestPartNode.addChild({title: 'Content Length: ' + value(part.contentLength), key: request.id + 'requestPart' + part.id + 'contentLength'});
								requestPartNode.addChild({title: 'Actual Content Length: ' + value(part.actualContentLength), key: request.id + 'requestPart' + part.id + 'actualContentLength'});
								if (part.actualContentLength > 0) {
									requestPartNode.addChild({title: 'Content >>', 
										monitorIsTextContext: part.textContent, 
										monitorPrettyPrint: false,
										monitorContentType: part.contentType,
										monitorNodeType: 'partContent', isLazy: false, isFolder: false,
										partId: part.id, key: request.id + 'requestPart' + part.id + 'content'});
									if (part.prettyPrint) {
										requestPartNode.addChild({title: 'Content Pretty Print >>', 
											monitorIsTextContext: part.textContent, 
											monitorPrettyPrint: true,
											monitorNodeType: 'partContent', isLazy: false, isFolder: false,
											partId: part.id, key: request.id + 'requestPart' + part.id + 'content'});
									}
								}
							} else {
								requestPartNode.addChild({title: 'Value: ' + value(part.value), key: request.id + 'requestPart' + part.id + 'value'});
							}
							// Headers
							if (part.headers && part.headers.length > 0) {
								var requestPartHeadersNode = requestPartNode.addChild({title: 'Headers', key: request.id + 'requestPart' + part.id + 'headers', isFolder: true});
								$.each(part.headers, function(index, header) {
									requestPartHeadersNode.addChild({title: header.name + ': ' + value(header.value), key: request.id + 'requestPart' + part.id + 'header' + header.id + 'name'});
								});
							}
						}
					}
					// Form
					if (request.formParameters && request.contentType && request.contentType.toLowerCase().indexOf('application/x-www-form-urlencoded') == 0) {
						var requestFormParametersNode = requestNode.addChild({title: 'Form Parameters', key: request.id + 'requestFormParameters', isFolder: true});
						for (var i=0; i<request.formParameters.length; i++) {
							var formParameter = request.formParameters[i];
							var requestFormParameterNode = requestFormParametersNode.addChild({title: 'Form Parameter', key: request.id + 'requestFormParameter' + formParameter.id, isFolder: true});
							requestFormParameterNode.addChild({title: 'Name: ' + value(formParameter.name), key: request.id + 'requestFormParameter' + formParameter.id + 'name'});
							var requestFormParameterValuesNode = requestFormParameterNode.addChild({title: 'Form Parameter Values', key: request.id + 'requestFormParameterValues' + formParameter.id, isFolder: true});
							for (var j=0; j<formParameter.values.length; j++) {
								var formParameterValue = formParameter.values[j];
								requestFormParameterValuesNode.addChild({title: 'Value: ' + value(formParameterValue.value), key: request.id + 'requestFormParameterValue' + formParameterValue.id + 'value'});
							}
						}
					}
					// Response
					if (request.response) {
						var response = request.response;
						var responseNode = requestnode.addChild({title: 'Response', key: response.id, isFolder: true});
						//responseNode.addChild({title: 'Response: ' + value(response.method), key: response.id + 'method'});
						if (response.status != null) {
							var status = value(response.status);
							if (response.statusDescription != null) {
								status = status + " - " + response.statusDescription;
							}
							responseNode.addChild({title: 'Status: ' + status, key: response.id + 'responseStatus'});
						}
						if (response.characterEncoding != null) {
							responseNode.addChild({title: 'Character Encoding: ' + value(response.characterEncoding), key: response.id + 'responseCharacterEncoding'});
						}
						if (response.contentType != null) {
							responseNode.addChild({title: 'Content Type: ' + value(response.contentType), key: response.id + 'responseContentType'});
						}
						if (response.locale != null) {
							responseNode.addChild({title: 'Locale: ' + response.locale, key: response.id + 'responseLocale'});
						}
						responseNode.addChild({title: 'Content Length: ' + value(response.contentLength), key: response.id + 'responseContentLength'});
						if (response.contentLength > 0) {
							responseNode.addChild({title: 'Content >>', 
								monitorIsTextContext: response.textContent, 
								monitorPrettyPrint: false,
								monitorContentType: response.contentType,
								monitorNodeType: 'responseContent', isLazy: false, isFolder: false,
								responseId: response.id, key: response.id + 'responseContent'});
							if (response.prettyPrint) {
								responseNode.addChild({title: 'Content Pretty Print >>', 
									monitorIsTextContext: response.textContent, 
									monitorPrettyPrint: true,
									monitorContentType: response.contentType,
									monitorNodeType: 'responseContent', isLazy: false, isFolder: false,
									responseId: response.id, key: response.id + 'responseContent'});
							}
						}
						// Headers
						if (response.headers && response.headers.length > 0) {
							var responseHeadersNode = responseNode.addChild({title: 'Headers', key: response.id + 'responseHeaders', isFolder: true});
							$.each(response.headers, function(index, header) {
								responseHeadersNode.addChild({title: header.name + ': ' + value(header.value), key: response.id + 'responseHeader' + header.id});
							});
						}
						// Cookies
						if (response.cookies && response.cookies.length > 0) {
							var responseCookiesNode = responseNode.addChild({title: 'Cookies', key: response.id + 'responseCookies', isFolder: true});
							$.each(response.cookies, function(index, cookie) {
								var responseCookieNode = responseCookiesNode.addChild(
										{title: 'Cookie Name: ' + value(cookie.name), key: response.id + 'responseCookie' + cookie.name, isFolder: true});
								for (var name in cookie) {
									if (name != 'id') {
										responseCookieNode.addChild({title: name + ': ' + value(cookie[name]), key: response.id + 'responseCookie' + name});
									}
								}
							});
						}
					}
					
					requestnode.addChild({title: 'Logging', key: request.id + 'logging', isFolder: true,
						isLazy: true,
						requestId: request.requestId,
						monitorNodeType: 'loggingContent'});
				}
				
	    		renderEntity(entity);

			};	
		
			scope.$watch(function () {
				return ngModel.$modelValue;
			}, function(newValue) {
				if (newValue != null && newValue != '') {
					renderDynatree($(element), newValue);
				}
			});

		}
	}
});
