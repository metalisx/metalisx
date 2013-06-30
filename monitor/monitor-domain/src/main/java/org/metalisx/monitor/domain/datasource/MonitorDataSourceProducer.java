package org.metalisx.monitor.domain.datasource;

import javax.annotation.Resource;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.sql.DataSource;

@Named
public class MonitorDataSourceProducer {

	@Resource(mappedName="java:/jdbc/monitorDS")
	private DataSource dataSource;
	
	@Produces
	@MonitorDataSource
	public DataSource getDataSource() {
		return dataSource;
	}

}
