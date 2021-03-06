package org.metalisx.common.rest.service;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Path;

import org.metalisx.common.rest.service.AbstractEntityRestService;


@Stateless
@Path("/crud")
public class CrudRestService extends AbstractEntityRestService {

    @PersistenceContext(unitName="crudPU")
	private EntityManager entityManager;
	
    @PostConstruct
    public void postConstruct() {
    	entityClasses.add("org.metalisx.common.domain.model.User");
    	setEntityManager(entityManager);
    }
    
}
