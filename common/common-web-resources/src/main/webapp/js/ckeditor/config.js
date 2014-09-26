/**
 * @license Copyright (c) 2003-2013, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.md or http://ckeditor.com/license
 */

CKEDITOR.editorConfig = function( config ) {
	// Define changes to default configuration here. For example:
	// config.language = 'fr';
	// config.uiColor = '#AADC6E';

	// Disable autofocus
	config.startupFocus = false;
	
	// Disable the advanced content filter so we can use inline Javascript and styles.
    config.allowedContent = true;
    
    // Configure file browser
	var filebrowserRoot = '/v2/console/filebrowser';
    config.filebrowserBrowseUrl = filebrowserRoot + '/filebrowser.html?caller=CKEditor';
    config.filebrowserImageBrowseUrl = filebrowserRoot + '/filebrowser.html?caller=CKEditor';
    config.filebrowserFlashBrowseUrl = filebrowserRoot + '/filebrowser.html?caller=CKEditor';
    config.filebrowserWindowWidth = '640';
    config.filebrowserWindowHeight = '480';

    // Remove some plugins
    config.removePlugins = 'flash, spellingchecker, forms, language'; 
    
    // Enter key means br not p
    config.enterMode = CKEDITOR.ENTER_BR; 
    // Paragraphs are now made by pressing shift and enter together instead
    config.shiftEnterMode = CKEDITOR.ENTER_P;
    
};
