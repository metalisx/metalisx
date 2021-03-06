package org.metalisx.common.domain.dao;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.metalisx.common.domain.dto.ContextDto;
import org.metalisx.common.domain.dto.Limit;
import org.metalisx.common.domain.dto.OrderBy;
import org.metalisx.common.domain.dto.PageContextDto;
import org.metalisx.common.domain.dto.PageDto;
import org.metalisx.common.domain.filter.AbstractFilter;
import org.metalisx.common.domain.model.User;
import org.metalisx.common.domain.query.PagedQueryProvider;
import org.metalisx.common.domain.query.PagedQueryProviderInterface;
import org.metalisx.common.domain.query.SimpleQuery;
import org.metalisx.common.domain.utils.DatePrecision;
import org.metalisx.common.domain.utils.InterfaceDateRange;
import org.metalisx.common.domain.utils.JpaUtils;
import org.metalisx.common.test.domain.TransactionRollbackTest;

@RunWith(Arquillian.class)
public class AbstractDaoTest extends TransactionRollbackTest {

    @Inject
    private DefaultDao defaultDao;

    public AbstractDaoTest() {
    }

    @Deployment
    public static Archive<?> createTestArchive() {
        return ShrinkWrap.create(JavaArchive.class, "test.jar").addClasses(
                // DAO's
                AbstractDao.class,
                // Dto
                ContextDto.class, Limit.class, OrderBy.class, PageContextDto.class, PageDto.class,
                // Filter
                AbstractFilter.class,
                // Utils
                DatePrecision.class, JpaUtils.class, InterfaceDateRange.class,
                // Query
                PagedQueryProvider.class, PagedQueryProviderInterface.class, SimpleQuery.class,
                // Test 
                User.class,
                DefaultDao.class,
                // Test supporting classes
                TransactionRollbackTest.class)
                .addAsManifestResource("META-INF/test-persistence.xml", ArchivePaths.create("persistence.xml"))
                .addAsManifestResource("META-INF/test-beans.xml", ArchivePaths.create("beans.xml"));
    }

    @Test
    public void testPersistAndFindById() {
    	User user = new User();
    	user.setName("Peter Parker");
    	
        user = defaultDao.persist(User.class, user);
        assertNotNull(user.getId());

        User foundUser = defaultDao.findById(User.class, user.getId());
        assertNotNull("Object not found when using findById.", foundUser);
    }
    
    @Test
    public void testRemove() {
    	User user = new User();
    	user.setName("Peter Parker");
    	
        user = defaultDao.persist(User.class, user);
        assertNotNull(user.getId());

        User foundUser = defaultDao.findById(User.class, user.getId());
        assertNotNull("Object not found when using findById.", foundUser);
        
        defaultDao.remove(User.class, user.getId());
        foundUser = defaultDao.findById(User.class, user.getId());
        assertNull("Object found when using findById.", foundUser);
    }
    
}
