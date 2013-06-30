package org.metalisx.crud.rest.service;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Path;


@Stateless
@Path("/crud")
public class CrudRestService extends AbstractCrudRestService {

    @PersistenceContext(unitName="crudPU")
	private EntityManager entityManager;
	
    @PostConstruct
    public void postConstruct() {
    	setEntityManager(entityManager);
    }

}
