package org.metalisx.common.domain.dao;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Class extending the {@link AbstractEntityDao} class for providing
 * the default entity manager.
 */
@Named
public class DefaultDao extends AbstractDao {

	@PersistenceContext
	protected EntityManager entityManager;

	@PostConstruct
	public void postConstruct() {
		setEntityManager(entityManager);
	}
	
}
