package org.metalisx.monitor.domain.dao;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.metalisx.common.domain.dao.AbstractDao;
import org.metalisx.monitor.domain.entitymanager.MonitorEntityManager;

/**
 * Class extending the {@link AbstractDao} class for providing the default
 * entity manager to {@link AbstractDao}.
 */
public class MonitorDao extends AbstractDao {

	@Inject
	@MonitorEntityManager
	protected EntityManager entityManager;

	@PostConstruct
	public void postConstruct() {
		this.setEntityManager(entityManager);
	}

}
