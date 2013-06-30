package org.metalisx.common.domain.service;

import static org.junit.Assert.assertNotNull;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.metalisx.common.domain.dao.AbstractDao;
import org.metalisx.common.domain.dao.AbstractEntityGenericDao;
import org.metalisx.common.domain.dao.AbstractGenericDao;
import org.metalisx.common.domain.dao.DefaultEntityGenericDao;
import org.metalisx.common.domain.dao.UserEntityGenericDao;
import org.metalisx.common.domain.dto.ContextDto;
import org.metalisx.common.domain.dto.Limit;
import org.metalisx.common.domain.dto.OrderBy;
import org.metalisx.common.domain.dto.PageContextDto;
import org.metalisx.common.domain.dto.PageDto;
import org.metalisx.common.domain.filter.AbstractFilter;
import org.metalisx.common.domain.model.AbstractEntity;
import org.metalisx.common.domain.model.UserAbstractEntity;
import org.metalisx.common.domain.query.PagedQueryProvider;
import org.metalisx.common.domain.query.PagedQueryProviderInterface;
import org.metalisx.common.domain.query.SimpleQuery;
import org.metalisx.common.domain.utils.DatePrecision;
import org.metalisx.common.domain.utils.InterfaceDateRange;
import org.metalisx.common.domain.utils.JpaUtils;
import org.metalisx.common.test.domain.TransactionRollbackTest;

@RunWith(Arquillian.class)
public class AbstractServiceTest extends TransactionRollbackTest {

    @EJB
    private UserService userService;

    public AbstractServiceTest() {
    }

    @Deployment
    public static Archive<?> createTestArchive() {
        return ShrinkWrap.create(JavaArchive.class, "test.jar").addClasses(
        		// Domain
		        AbstractEntity.class,
		        // DAO's
		        AbstractDao.class, AbstractGenericDao.class, AbstractEntityGenericDao.class,
                // Services
                AbstractService.class, UserService.class,
                // Dto
                ContextDto.class, Limit.class, OrderBy.class, PageContextDto.class, PageDto.class,
                // Filter
                AbstractFilter.class,
                // Utils
                DatePrecision.class, JpaUtils.class, InterfaceDateRange.class,
                // Query
                PagedQueryProvider.class, PagedQueryProviderInterface.class, SimpleQuery.class,
		        // Teset
                UserAbstractEntity.class,
		        DefaultEntityGenericDao.class, UserEntityGenericDao.class,
		        UserService.class,
                // Test supporting classes
                TransactionRollbackTest.class)
                .addAsManifestResource("META-INF/test-persistence.xml", ArchivePaths.create("persistence.xml"))
                .addAsManifestResource("META-INF/test-beans.xml", ArchivePaths.create("beans.xml"));
    }

    @Test
    public void testPersistAndFindById() {
    	UserAbstractEntity user = new UserAbstractEntity();
    	user.setName("Peter Parker");
    	
        user = userService.persist(user);
        assertNotNull(user.getId());

        UserAbstractEntity foundUser = userService.findById(user.getId());
        assertNotNull("Object not found when using findById.", foundUser);
    }

}
