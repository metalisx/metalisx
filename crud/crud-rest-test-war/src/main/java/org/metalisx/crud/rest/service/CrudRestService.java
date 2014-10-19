package org.metalisx.crud.rest.service;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Path;

import org.metalisx.common.rest.service.AbstractRestService;
import org.metalisx.crud.domain.model.Test;
import org.metalisx.crud.domain.model.TestDocument;
import org.metalisx.crud.domain.model.TestTextarea;

@Stateless
@Path("/crud")
public class CrudRestService extends AbstractRestService {

    @PersistenceContext(unitName="crudPU")
	private EntityManager entityManager;
	
    @PostConstruct
    public void postConstruct() {
    	setEntityManager(entityManager);
    	// Available entities
    	this.entityClasses.add(Test.class.getName());
    	this.entityClasses.add(TestDocument.class.getName());
    	this.entityClasses.add(TestTextarea.class.getName());
    }

}
