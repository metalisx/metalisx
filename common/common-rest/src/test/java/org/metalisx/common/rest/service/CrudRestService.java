package org.metalisx.common.rest.service;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Path;

import org.metalisx.common.rest.service.AbstractRestService;


@Stateless
@Path("/crud")
public class CrudRestService extends AbstractRestService {

    @PersistenceContext(unitName="crudPU")
	private EntityManager entityManager;
	
    @PostConstruct
    public void postConstruct() {
    	setEntityManager(entityManager);
    }
    
}
