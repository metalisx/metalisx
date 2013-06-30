package org.metalisx.common.domain.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.metalisx.common.domain.model.User;
import org.metalisx.common.domain.model.UserAbstractEntity;

public class JpaUtilsTest {

	@Test
	public void isNullOrEmptyTest() {
		assertTrue(JpaUtils.isNullOrEmpty(""));
		assertTrue(JpaUtils.isNullOrEmpty(null));
		assertFalse(JpaUtils.isNullOrEmpty("some text"));
	}
	
	@Test
	public void likeTest() {
		String value = JpaUtils.like("some text");
		assertEquals("%some text%", value);
	}
	
	@Test
	public void getAbstractEntityClassTest() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		String className = JpaUtils.toAbstractEntityClass(UserAbstractEntity.class.getName()).getCanonicalName();
		assertEquals(UserAbstractEntity.class.getName(), className);
	}
	
	@Test(expected=IllegalStateException.class)
	public void getAbstractEntityClassExceptionTest() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		String className = JpaUtils.toAbstractEntityClass(User.class.getName()).getCanonicalName();
		assertEquals(UserAbstractEntity.class.getName(), className);
	}
	
	@Test
	public void getClassTest() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		String className = JpaUtils.toClass(User.class.getName()).getCanonicalName();
		assertEquals(User.class.getName(), className);
	}
	
}
