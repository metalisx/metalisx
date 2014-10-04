application.service('requestService', RequestService);

application.service('requestRenderer', RequestRenderer);

function RequestService(crudService) {
	
	var treeEndpoint = '../rest/logs/request/tree/';

	this.getTree = function(requestId, onsuccess) {
		crudService.get(treeEndpoint + requestId, null, {onsuccess: onsuccess});
	};

}

function RequestRenderer() {

	var $container = $('#requestDetail');

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

	this.render = function(result) {
		if (result && result.item && result.item.children) {
			var $tree = $('<div class="tree"></div>');
			$container.append($tree);
			$tree.dynatree();
			var rootNode = $tree.dynatree("getRoot");
			toNodesChildren(rootNode, result.item.children);
		}
	};

}

function RequestController($scope, requestService, requestRenderer) {

	$scope.requestId = $.getUrlValue('requestId');

	function init() {
		requestService.getTree($scope.requestId, function(result) {
			requestRenderer.render(result);
		});
	}
	
	init();

}
