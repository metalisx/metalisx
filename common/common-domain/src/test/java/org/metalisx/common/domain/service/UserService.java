package org.metalisx.common.domain.service;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.metalisx.common.domain.dao.UserEntityGenericDao;
import org.metalisx.common.domain.model.UserAbstractEntity;

@Stateless
public class UserService extends AbstractService {

	@Inject
	private UserEntityGenericDao userEntityGenericDao;
	
	public UserAbstractEntity persist(UserAbstractEntity user) {
		return userEntityGenericDao.persist(user);
	}
	
	public UserAbstractEntity findById(Long id) {
		return userEntityGenericDao.findById(id);
	}
	
}
