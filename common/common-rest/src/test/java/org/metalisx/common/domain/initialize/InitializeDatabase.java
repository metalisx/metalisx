package org.metalisx.common.domain.initialize;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.metalisx.common.domain.model.User;

@Singleton
@Startup
public class InitializeDatabase {

	@PersistenceContext(unitName="crudPU")
	private EntityManager entityManager;

	public InitializeDatabase() {
	}

	@PostConstruct
	public void postConstruct() {
		User user = new User();
		user.setId(1L);
		user.setName("Peter Parker");
		user = entityManager.merge(user);
		entityManager.flush();
	}

}
