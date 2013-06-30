package org.metalisx.monitor.domain.startup;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.sql.DataSource;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

import org.hibernate.exception.SQLGrammarException;
import org.metalisx.monitor.domain.datasource.MonitorDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for installing the Monitor artifacts in the database.
 */
@Singleton
@Startup
@TransactionManagement(TransactionManagementType.BEAN)
public class MonitorDatabase {

	private static final Logger logger = LoggerFactory.getLogger(MonitorDatabase.class);

	private static final String VALIDATION_QUERY = "select count(1) from monitorlog";

	private static final String CHANGELOG = "db/db.changelog-master.xml";
	
	private static final String CONTEXT_INITIAL_DDL = "initial-ddl";
	
	private static final String CONTEXT_INITIAL_DML = "initial-dml";

	@Inject
	@MonitorDataSource
	private DataSource dataSource;

	public MonitorDatabase() {
	}

	@PostConstruct
	public void init() throws SQLException, LiquibaseException {
		install();
	}

	/**
	 * Crude test if the Monitor artifact are installed in the database of the
	 * injected data source.
	 */
	public boolean isInstalled() throws SQLException {
		boolean installed = false;
		try {
			Connection connection = dataSource.getConnection();
			Statement statement = connection.createStatement();
			statement.executeQuery(VALIDATION_QUERY);
			installed = true;
		} catch (SQLGrammarException e) {
			logger.info("Validation error: The Monitor artifacts are not installed in the database.");
			logger.info(e.getMessage());
		}
		return installed;
	}

	/**
	 * Installs the Monitor artifacts in the database of the injected data
	 * source with Liquibase scripts.
	 * 
	 * @throws LiquibaseException
	 */
	public void install() throws SQLException, LiquibaseException {
		Connection connection = dataSource.getConnection();
		Liquibase liquibase = null;
		try {
			Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(
			        new JdbcConnection(connection));
			liquibase = new Liquibase(CHANGELOG, new ClassLoaderResourceAccessor(), database);
			liquibase.update(CONTEXT_INITIAL_DDL);
			liquibase.update(CONTEXT_INITIAL_DML);
		} finally {
			if (connection != null) {
				connection.rollback();
				connection.close();
			}
		}
	}

	public void exportSchemaDdl(String filename) throws SQLException, LiquibaseException, IOException {
		Connection connection = dataSource.getConnection();
		Liquibase liquibase = null;
		try {
			Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(
			        new JdbcConnection(connection));
			liquibase = new Liquibase(CHANGELOG, new ClassLoaderResourceAccessor(), database);
			FileWriter writer = new FileWriter(filename);
			liquibase.update("", writer);
		} finally {
			if (connection != null) {
				connection.rollback();
				connection.close();
			}
		}
	}

}
