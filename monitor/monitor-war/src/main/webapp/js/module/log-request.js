function LogRequestController($scope, $compile, $http, $location, $window, $routeParams, crudService, 
		applicationContext) {

	var treeEndpoint = '../rest/logs/request/tree/';

	$scope.entity = null;
	
	// Init
	
	function init() {
		$scope.initEntity();
	}

	// Entity
	
	$scope.initEntity = function() {
		var requestId = $routeParams.requestId;
		$scope.getEntity(requestId);
	}

	$scope.getEntity = function(requestId) {
		crudService.get(treeEndpoint + requestId, null, {onsuccess: function(result) {
    		$scope.entity = result.item;
    		$scope.$apply();
    	}});
    }
	
	init();

}

function RequestRenderer() {

	var $container = $('#requestDetail');

}

/**
 * Angular module :: ngcLogRequestDynatree
 * 
 * The Angular module ngcLogRequestDynatree provides the rendering
 * of the log tree with Dynatree.
 * 
 * Requires the DynaTree files for version 1.2.4:
 * - jquery.dynatree.min.js
 * - ui.dynatree.css
 */
(function(angular) {
	
	'use strict';

	// Module
	
	var ngcLogRequestDynatree = angular.module('ngcLogRequestDynatree', []);

	// Directives
	
	ngcLogRequestDynatree.directive('ngcLogRequestDynatree', function(applicationContext) {
		return {
			restrict: 'A',
			require: 'ngModel',
			link: function(scope, element, attrs, ngModel) {
				
				function toNodesChildren(node, children) {
					$.each(children, function(index, item) {
						var title = '<div><div class="duration">'  + item.monitorLog.duration + ' ms</div><div class="message">' + item.monitorLog.message + '</div></div>';
						if (item.children && item.children.length > 0) {
							var monitorLogNode = node.addChild({title: title, key: item.monitorLog.id + 'log', isFolder: true});
							toNodesChildren(monitorLogNode, item.children);
						} else {
							node.addChild({title: title, key: item.monitorLog.id + 'log'});
						}
					});
				}
	
				var renderDynatree = function($element, entity) {
					if (entity && entity.children) {
						var $tree = $('<div class="tree"></div>');
						$element.append($tree);
						$tree.dynatree({
							debugLevel: 0
						});
						var rootNode = $tree.dynatree("getRoot");
						toNodesChildren(rootNode, entity.children);
					}
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

})(window.angular);
