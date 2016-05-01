package org.metalisx.monitor.domain.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.metalisx.monitor.domain.model.User;
import org.metalisx.monitor.profiler.interceptor.Profile;

@Profile
@Stateless
public class UserService {

	@PersistenceContext(unitName = "monitorTestPU")
	protected EntityManager entityManager;

	public int clean() {
		Query query = entityManager.createQuery("delete from " + User.class.getSimpleName());
		return query.executeUpdate();
	}

	public void persist(User user) {
		entityManager.persist(user);
		entityManager.flush();
	}

	public List<User> findAll() {
		TypedQuery<User> query = entityManager.createQuery("select o from " + User.class.getSimpleName() + " o",
		        User.class);
		return query.getResultList();
	}

}
