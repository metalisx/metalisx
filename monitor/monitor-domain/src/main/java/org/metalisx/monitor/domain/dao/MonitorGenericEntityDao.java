package org.metalisx.monitor.domain.dao;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.metalisx.common.domain.dao.AbstractEntityGenericDao;
import org.metalisx.common.domain.model.AbstractEntity;
import org.metalisx.monitor.domain.entitymanager.MonitorEntityManager;

/**
 * Class extending the {@link AbstractEntityGenericDao} class for providing the
 * entity manager to {@link AbstractEntityGenericDao}.
 */
public class MonitorGenericEntityDao<T extends AbstractEntity, I extends Serializable> extends
        AbstractEntityGenericDao<T, I> {

	@Inject
	@MonitorEntityManager
	protected EntityManager entityManager;

	@PostConstruct
	public void postConstruct() {
		this.setEntityManager(entityManager);
	}

}
