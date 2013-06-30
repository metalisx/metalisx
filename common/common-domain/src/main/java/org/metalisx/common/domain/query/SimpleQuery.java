package org.metalisx.common.domain.query;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

import org.metalisx.common.domain.dto.Limit;

/**
 * Simple class container for centralized storage of JPQL query information and
 * to construct a typed query from this information.
 * 
 * This class is created mainly to support dynamically creating queries for
 * filtering data sets to optimize the execution time of the query. It relies on
 * constructing queries yourself, instead of using the JPA criteria builder or a
 * third party library. As these are easy to use, they lack in readability when
 * complex queries are required. Which is mainly visible in queries for
 * filtering data sets.
 * 
 * @author stefan.oude.nijhuis
 */
public class SimpleQuery {

	private static final String SELECT = "select";
	private static final String FROM = "from";
	private static final String WHERE = "where";
	private static final String ORDER_BY = "order by";
	private static final String GROUP_BY = "group by";

	private String select = null;

	private String from = null;

	private String where = null;

	private String groupBy = null;

	private String orderBy = null;

	private Limit limit = null;

	private Map<String, Object> parameters = new HashMap<String, Object>();

	public SimpleQuery() {
	}

	public <T> TypedQuery<T> getTypedQuery(Class<T> type, EntityManager entityManager) {
		TypedQuery<T> typedQuery = entityManager.createQuery(asQueryStringWithoutLimit(), type);
		queryLimit(typedQuery, limit);
		queryParameters(typedQuery, parameters);
		return typedQuery;
	}

	public String asQueryStringWithoutLimit() {
		String queryString = "";
		queryString += select == null || "".equals(select) ? "" : SELECT + " " + select;
		queryString += from == null || "".equals(from) ? "" : " " + FROM + " " + from;
		queryString += where == null || "".equals(where) ? "" : " " + WHERE + " " + where;
		queryString += groupBy == null || "".equals(groupBy) ? "" : " " + GROUP_BY + " " + groupBy;
		queryString += orderBy == null || "".equals(orderBy) ? "" : " " + ORDER_BY + " " + orderBy;
		return queryString;
	}
	
	private void queryLimit(Query query, Limit queryLimit) {
		if (queryLimit != null) {
			query.setFirstResult(queryLimit.getStart());
			query.setMaxResults(queryLimit.getCount());
		}
	}

	private void queryParameters(Query query, Map<String, Object> parameters) {
		Set<String> keySet = parameters.keySet();
		Iterator<String> iterator = keySet.iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			if (parameters.get(key) instanceof Date) {
				query.setParameter(key, (Date) parameters.get(key), TemporalType.TIMESTAMP);
			} else {
				query.setParameter(key, parameters.get(key));
			}
		}
	}

	public SimpleQuery addParameter(String key, Object value) {
		parameters.put(key, value);
		return this;
	}

	public SimpleQuery addSelect(String select) {
		this.select = (this.select == null ? "" : this.select) + " " + select;
		return this;
	}

	public SimpleQuery addFrom(String from) {
		this.from = (this.from == null ? "" : this.from) + " " + from;
		return this;
	}

	public SimpleQuery addWhereWithAnd(String where) {
		this.where = (this.where == null ? "" : this.where + " and") + " " + where;
		return this;
	}

	public SimpleQuery addOrderBy(String orderBy) {
		this.orderBy = (this.orderBy == null ? "" : this.orderBy + ",") + " " + orderBy;
		return this;
	}

	public SimpleQuery addGroupBy(String groupBy) {
		this.groupBy = (this.groupBy == null ? "" : this.groupBy + ",") + " " + groupBy;
		return this;
	}

	public SimpleQuery setLimit(Limit limit) {
		this.limit = limit;
		return this;
	}

	public SimpleQuery setSelect(String select) {
		this.select = select;
		return this;
	}

	public SimpleQuery setFrom(String from) {
		this.from = from;
		return this;
	}

	public SimpleQuery setWhere(String where) {
		this.where = where;
		return this;
	}

	public SimpleQuery setGroupBy(String groupBy) {
		this.groupBy = groupBy;
		return this;
	}

	public SimpleQuery setOrderBy(String orderBy) {
		this.orderBy = orderBy;
		return this;
	}
	
	public String toString() {
		return asQueryStringWithoutLimit() + (limit != null ? " [limit start=" + limit.getStart() + ", count=" + limit.getCount() + "]" : "");
	}

}
