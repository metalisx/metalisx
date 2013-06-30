package org.metalisx.monitor.domain.dao.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Named;
import javax.persistence.TypedQuery;

import org.metalisx.common.domain.query.SimpleQuery;
import org.metalisx.common.domain.utils.DatePrecision;
import org.metalisx.monitor.domain.dao.MonitorDao;
import org.metalisx.monitor.domain.dto.MonitorOverviewItem;
import org.metalisx.monitor.domain.filter.MonitorLogFilter;
import org.metalisx.monitor.domain.model.MonitorLog;
import org.metalisx.monitor.domain.query.MonitorLogFilterToSimpleQuery;

@Named
public class MonitorLogOverviewItemDao extends MonitorDao {

	private static Map<DatePrecision, String> precisionPart = new HashMap<DatePrecision, String>();

	MonitorLogFilterToSimpleQuery monitorLogFilterToSimpleQuery = new MonitorLogFilterToSimpleQuery();

	static {
		precisionPart.put(DatePrecision.YEAR, "year(o.logDate)");
		precisionPart.put(DatePrecision.MONTH, "year(o.logDate), month(o.logDate)");
		precisionPart.put(DatePrecision.DAY, "year(o.logDate), month(o.logDate), day(o.logDate)");
		precisionPart.put(DatePrecision.HOUR, "year(o.logDate), month(o.logDate), day(o.logDate)"
		        + ", hour(o.logDate)");
		precisionPart.put(DatePrecision.MINUTE, "year(o.logDate), month(o.logDate), day(o.logDate)"
		        + ", hour(o.logDate), minute(o.logDate)");
		precisionPart.put(DatePrecision.SECOND, "year(o.logDate), month(o.logDate), day(o.logDate)"
		        + ", hour(o.logDate), minute(o.logDate), second(o.logDate)");
		precisionPart.put(DatePrecision.MILLISECOND, "o.logDate");
	}

	public List<MonitorOverviewItem> findOverviewByFilter(MonitorLogFilter monitorLogFilter, DatePrecision datePrecision) {
		SimpleQuery simpleQuery = new SimpleQuery();
		simpleQuery.addSelect("new " + MonitorOverviewItem.class.getName() + "(count(o), "
		        + precisionPart.get(datePrecision) + ", avg(o.duration))");
		simpleQuery.addFrom(MonitorLog.class.getSimpleName() + " o");
		monitorLogFilterToSimpleQuery.processFilter(simpleQuery, monitorLogFilter);
		simpleQuery.addGroupBy(precisionPart.get(datePrecision));
		simpleQuery.addOrderBy(precisionPart.get(datePrecision));
		TypedQuery<MonitorOverviewItem> query = simpleQuery.getTypedQuery(MonitorOverviewItem.class, entityManager);
		return query.getResultList();
	}

}
