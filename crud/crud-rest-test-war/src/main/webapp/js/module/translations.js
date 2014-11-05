/**
 * Initialize a translation mapping of the class names 
 * to a human readable name. 
 */
application.run(function(translationService) {

	translationService.put({'code':'org.metalisx.crud.domain.model.Test', 'text':'Test'});
	translationService.put({'code':'org.metalisx.crud.domain.model.TestDocument', 'text':'Test document'});
	translationService.put({'code':'org.metalisx.crud.domain.model.TestTextarea', 'text':'Test textarea'});

})

