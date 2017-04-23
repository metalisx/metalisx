package org.metalisx.common.cdi.service;

import static org.junit.Assert.assertEquals;

import javax.ejb.EJB;

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

@RunWith(Arquillian.class)
public class UserServiceTest {

    @EJB
    private UserService userService;

    public UserServiceTest() {
    }

    @Deployment
    public static Archive<?> createTestArchive() {
        return ShrinkWrap.create(JavaArchive.class, "test.jar").addClasses(
        		// Extension
        		AnnotatedTypeWrapper.class,	
        		LogExtension.class,
	        	// Interceptor
                Log.class,
                LogInterceptor.class,
                // Services
                AbstractUserService.class,
                UserService.class,
		        // Teset
		        UserServiceTest.class)
                .addAsManifestResource("META-INF/test-beans.xml", ArchivePaths.create("beans.xml"))
                .addAsManifestResource("META-INF/services/javax.enterprise.inject.spi.Extension", ArchivePaths.create("services/javax.enterprise.inject.spi.Extension"));
    }

    @Test
    public void testPersistAndFindById() {
        String message = userService.hello("Peter");
        assertEquals("Hello Peter", message);
    }

}
