(function(angular) {
	
	'use strict';

	// Module
	
	var application = angular.module('application');

	// Controller
	
	application.controller('MessagesModuleController', 
		function MessagesModuleController($scope, applicationContext, ngcMessagesService) {
		
			$scope.loadMessagesParameter = "your name";
			$scope.putMultipleMessagesParameter = "your name";
			$scope.putSingleMessageParameter = "your name";
			
			// Method load

			$scope.loadMessagesEnglish = function($event) {
				$event.stopPropagation();
				$event.preventDefault();
				var messagesUrl = applicationContext.contextPath + '/messages/messages_en-us.txt';
				ngcMessagesService.load(messagesUrl);
				ngcMessagesService.setLanguage('en-us');
			};
			
			$scope.loadMessagesDutch = function($event) {
				$event.stopPropagation();
				$event.preventDefault();
				var messagesUrl = applicationContext.contextPath + '/messages/messages_nl.txt';
				ngcMessagesService.load(messagesUrl);
				ngcMessagesService.setLanguage('nl');
			};
			
			// Method put :: multi messages
			
			$scope.putMultipleMessagesEnglish = function($event) {
				$event.stopPropagation();
				$event.preventDefault();
				ngcMessagesService.put({
					'language' : 'en-us',
					'messages' : [ {
						'code' : 'testLabel1',
						'text' : 'Initial'
					}, {
						'code' : 'testLabel2',
						'text' : 'Initial'
					}, {
						'code' : 'testLabel3',
						'text' : 'Hello'
					}, {
						'code' : 'testLabel4',
						'text' : 'Hello {{putMultipleMessagesParameter}}'
					}, {
						'code' : 'testLabel5',
						'text' : 'Initial'
					}, {
						'code' : 'testLabel6',
						'text' : 'Initial'
					} ]
				});
				ngcMessagesService.setLanguage('en-us');
			};
			
			$scope.putMultipleMessagesDutch = function($event) {
				$event.stopPropagation();
				$event.preventDefault();
				ngcMessagesService.put({
					'language' : 'nl',
					'messages' : [ {
						'code' : 'testLabel1',
						'text' : 'Initial'
					}, {
						'code' : 'testLabel2',
						'text' : 'Initial'
					}, {
						'code' : 'testLabel3',
						'text' : 'Hallo'
					}, {
						'code' : 'testLabel4',
						'text' : 'Hallo {{putMultipleMessagesParameter}}'
					}, {
						'code' : 'testLabel5',
						'text' : 'Initial'
					}, {
						'code' : 'testLabel6',
						'text' : 'Initial'
					} ]
				});
				ngcMessagesService.setLanguage('nl');
			};
			
			// Method put :: single messages
			
			$scope.putSingleMessageEnglish = function($event) {
				$event.stopPropagation();
				$event.preventDefault();
				ngcMessagesService.put('en-us', 'testLabel1', 'Initial');
				ngcMessagesService.put('en-us', 'testLabel2', 'Initial');
				ngcMessagesService.put('en-us', 'testLabel3', 'Initial');
				ngcMessagesService.put('en-us', 'testLabel4', 'Initial');
				ngcMessagesService.put('en-us', 'testLabel5', 'Hello');
				ngcMessagesService.put('en-us', 'testLabel6', 'Hello {{putSingleMessageParameter}}');
				ngcMessagesService.setLanguage('en-us');
			};
		
			$scope.putSingleMessageDutch = function($event) {
				$event.stopPropagation();
				$event.preventDefault();
				ngcMessagesService.put('nl', 'testLabel1', 'Initial');
				ngcMessagesService.put('nl', 'testLabel2', 'Initial');
				ngcMessagesService.put('nl', 'testLabel3', 'Initial');
				ngcMessagesService.put('nl', 'testLabel4', 'Initial');
				ngcMessagesService.put('nl', 'testLabel5', 'Hallo');
				ngcMessagesService.put('nl', 'testLabel6', 'Hallo {{putSingleMessageParameter}}');
				ngcMessagesService.setLanguage('nl');
			};
			
		}

	);
	
})(window.angular);
