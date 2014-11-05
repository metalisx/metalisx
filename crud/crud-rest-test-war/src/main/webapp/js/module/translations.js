/**
 * Initialize a translation mapping of the class names 
 * to a human readable name. 
 */
application.run(function(translationService) {

	var translations = [
	                    {'code':'org.metalisx.crud.domain.model.Test', 'text':'Test'},
	                    {'code':'org.metalisx.crud.domain.model.TestDocument', 'text':'Test document'},
	                    {'code':'org.metalisx.crud.domain.model.TestTextarea', 'text':'Test textarea'}
	                   ];
	
	for (var i=0; i<translations.length; i++) {
		translationService.put(translations[i]);
	}

})
