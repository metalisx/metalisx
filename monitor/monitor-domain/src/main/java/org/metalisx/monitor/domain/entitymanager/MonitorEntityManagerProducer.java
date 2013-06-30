package org.metalisx.monitor.domain.entitymanager;

import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Named
public class MonitorEntityManagerProducer {

	@PersistenceContext(unitName = "monitorPU")
	protected EntityManager entityManager;

	@Produces
	@MonitorEntityManager
	public EntityManager getEntityManager() {
		return entityManager;
	}

}
