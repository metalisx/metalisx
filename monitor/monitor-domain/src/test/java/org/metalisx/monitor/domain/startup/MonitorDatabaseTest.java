package org.metalisx.monitor.domain.startup;

import static org.junit.Assert.assertTrue;

import java.sql.SQLException;

import javax.inject.Inject;

import liquibase.exception.LiquibaseException;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.metalisx.monitor.domain.Deployments;
import org.metalisx.monitor.domain.startup.MonitorDatabase;

@RunWith(Arquillian.class)
public class MonitorDatabaseTest {

	@Inject
	private MonitorDatabase monitorDatabase;
	
	@Deployment
	public static Archive<?> createTestArchive() {
		return Deployments.createDeployment();
	}

	@Test
	public void installTest() throws SQLException  {
		assertTrue(monitorDatabase.isInstalled());
	}

	/**
	 * Check if a second call to the install method does not throw an error.
	 */
	@Test
	public void secondInstallTest() throws SQLException, LiquibaseException {
		monitorDatabase.install();
		assertTrue(monitorDatabase.isInstalled());
	}
	
}
