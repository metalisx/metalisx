package org.metalisx.common.cdi.service;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import javax.ejb.EJB;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.metalisx.common.cdi.extension.AnnotatedTypeWrapper;
import org.metalisx.common.cdi.extension.LogExtension;
import org.metalisx.common.cdi.interceptor.Log;
import org.metalisx.common.cdi.interceptor.LogInterceptor;
import org.metalisx.common.cdi.utils.CdiUtils;

@RunWith(Arquillian.class)
public class UserServiceTest {

	@Inject
	private BeanManager beanManager;

	@EJB
	private UserService userService;

	public UserServiceTest() {
	}

	@Deployment
	public static Archive<?> createTestArchive() {
		return ShrinkWrap.create(JavaArchive.class, "test.jar")
				.addClasses(
						// Extension
						AnnotatedTypeWrapper.class, LogExtension.class,
						// Interceptor
						Log.class, LogInterceptor.class,
						// Services
						AbstractUserService.class, UserService.class,
						// Utils
						CdiUtils.class,
						// Test
						UserServiceTest.class)
				.addAsManifestResource("META-INF/test-beans.xml", ArchivePaths.create("beans.xml"))
				.addAsManifestResource("META-INF/services/javax.enterprise.inject.spi.Extension",
						ArchivePaths.create("services/javax.enterprise.inject.spi.Extension"));
	}

	/**
	 * Check if the UserService is found as managed bean by the BeanManager.
	 */
	@Test
	public void testFindUserServiceWithAnyAnnotation() {
		Set<Bean<?>> beans = this.beanManager.getBeans(UserService.class, new AnnotationLiteral<Any>() {
		});
		assertEquals(1, beans.size());
	}

	/**
	 * Check if the UserService is found as managed bean by the BeanManager with
	 * the Log annotation.
	 * 
	 * The UserService should be found but is not. This is because annotations
	 * added in an extension during runtime are not found through the
	 * BeanManager, just as they are not found with the InvocationContext in an
	 * Interceptor. Hopefully there will be a solution to this problem in a new
	 * release.
	 */
	@Test(expected = RuntimeException.class)
	public void testFindUserServiceWithLogAnnotation() {
		Set<Bean<?>> beans = this.beanManager.getBeans(UserService.class, new AnnotationLiteral<Log>() {
		});
		assertEquals(1, beans.size());
	}

	@Test
	public void testPersistAndFindById() {
		// See the log file for the log statement, it is there beside the fact
		// the BeanManager can not find the service with the Log annotataion.
		String message = userService.hello("Peter");
		assertEquals("Hello Peter", message);
	}

}
