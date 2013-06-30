package org.metalisx.common.domain.dao;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.metalisx.common.domain.model.AbstractEntity;

/**
 * Class extending the {@link AbstractEntityGenericDao} class for providing
 * the default entity manager.
 */
public class DefaultEntityGenericDao<T extends AbstractEntity, I extends Serializable> extends AbstractEntityGenericDao<T, I> {

	@PersistenceContext
	protected EntityManager entityManager;

	@PostConstruct
	public void postConstruct() {
		this.setEntityManager(entityManager);
	}
	
}
