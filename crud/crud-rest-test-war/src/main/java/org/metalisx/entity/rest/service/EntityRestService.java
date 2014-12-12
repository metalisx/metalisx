package org.metalisx.entity.rest.service;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Path;

import org.metalisx.common.rest.service.AbstractEntityRestService;
import org.metalisx.entity.domain.model.Test;
import org.metalisx.entity.domain.model.TestDocument;
import org.metalisx.entity.domain.model.TestTextarea;

@Stateless
@Path("/entity")
public class EntityRestService extends AbstractEntityRestService {

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
