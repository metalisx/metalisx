package org.metalisx.common.test.domain;

import javax.inject.Inject;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.junit.After;
import org.junit.Before;

/**
 * Abstract class to support transaction rollback after running a unit test.
 * 
 * @author Stefan.Oude.Nijhuis
 * 
 */
public abstract class TransactionRollbackTest {

    @Inject
    private UserTransaction userTransaction;

    @Before
    public void startTransaction() throws NotSupportedException, SystemException {
        userTransaction.begin();
    }

    @After
    public void rollbackTransaction() throws IllegalStateException, SecurityException, SystemException {
        userTransaction.rollback();
    }

}
