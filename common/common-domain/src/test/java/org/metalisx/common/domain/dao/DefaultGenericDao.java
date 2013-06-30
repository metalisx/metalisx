package org.metalisx.common.domain.dao;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Class extending the {@link AbstractEntityGenericDao} class for providing
 * the default entity manager.
 */
public class DefaultGenericDao<T, I extends Serializable> extends AbstractGenericDao<T, I> {

	@PersistenceContext
	protected EntityManager entityManager;

	@PostConstruct
	public void postConstruct() {
		this.setEntityManager(entityManager);
	}
	
}
