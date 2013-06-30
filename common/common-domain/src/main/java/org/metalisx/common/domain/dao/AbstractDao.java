package org.metalisx.common.domain.dao;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.metalisx.common.domain.dto.ContextDto;
import org.metalisx.common.domain.dto.Limit;
import org.metalisx.common.domain.dto.OrderBy;
import org.metalisx.common.domain.dto.PageDto;
import org.metalisx.common.domain.query.PagedQueryProviderInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JPA class for basic CRUD data access operations for an entity.
 * 
 * This is not a class to handle all the possibilities you can encounter when
 * managing entities with JPA. It is only for the most basic operations, for
 * advanced queries you still need to create your own code.
 * 
 * This class functions as a marker class for all JPA DOA's in domain projects.
 * 
 * @author Stefan Oude Nijhuis
 * 
 */
public abstract class AbstractDao {

	private static final Logger logger = LoggerFactory.getLogger(AbstractDao.class);

	/** Alias for the entity in the query. */
	private static final String ENTITY_ALIAS = "o";

	private EntityManager entityManager;

	// Construcors

	public AbstractDao() {
		logger.debug("Initalized DAO ");
	}

	// Convenience methods

	/**
	 * Returns a list of all entities according to the Entities named query
	 * <code>namedQuery</code>. This method ensures a list of {@link T}s.
	 * 
	 * @param entityClass The entity class
	 * @param namedQuery The name of the named query as specified in the entity
	 * @return List of entities.
	 */
	public <T> List<T> findAllByNamedQuery(Class<T> entityClass, String namedQuery) {
		TypedQuery<T> typedQuery = getEntityManager().createNamedQuery(namedQuery, entityClass);
		List<T> resultList = typedQuery.getResultList();
		return resultList;
	}

	/**
	 * Returns a list of all persistent entities in no specific order and with
	 * no filter.
	 * 
	 * @param entityClass The entity class
	 * @return List of entities.
	 */
	public <T> List<T> findAll(Class<T> entityClass) {
		String queryString = "select " + ENTITY_ALIAS + " from " + entityClass.getSimpleName() + " " + ENTITY_ALIAS;
		TypedQuery<T> typedQuery = getEntityManager().createQuery(queryString, entityClass);
		List<T> resultList = typedQuery.getResultList();
		return resultList;
	}

	/**
	 * Returns a list of all entities according to the given Java Persistence
	 * query language statement <code>qlString</code>. This method ensures a
	 * list of {@link T}s.
	 * 
	 * @param entityClass The entity class
	 * @param qlString The Java Persistence query language statement to execute
	 * @return List of entities.
	 */
	public <T> List<T> findAll(Class<T> entityClass, String qlString) {
		TypedQuery<T> typedQuery = getEntityManager().createQuery(qlString, entityClass);
		List<T> resultList = typedQuery.getResultList();
		return resultList;
	}

	/**
	 * Returns a list of all entities according to the given Java Persistence
	 * typed query <code>typedQuery</code>. This method ensures a list of
	 * {@link T}s.
	 * 
	 * @param entityClass The entity class
	 * @param typedQuery The Java Persistence typed query to execute
	 * @return List of entities.
	 */
	public <T> List<T> findAll(Class<T> entityClass, TypedQuery<T> typedQuery) {
		return typedQuery.getResultList();
	}

	/**
	 * Returns a list of all entities according to the given order by and limit.
	 * This method ensures a list of {@link T}s.
	 * 
	 * @param entityClass The entity class
	 * @param queryOrderBy The order by information for the query
	 * @param queryLimit The limit information for the query
	 * @return List of entities.
	 */
	public <T> List<T> findAll(Class<T> entityClass, OrderBy queryOrderBy, Limit queryLimit) {
		String queryString = "select " + ENTITY_ALIAS + " from " + entityClass.getSimpleName() + " " + ENTITY_ALIAS;
		queryString += (queryOrderBy == null ? "" : " order by " + queryOrderBy.getAsString());
		TypedQuery<T> typedQuery = getEntityManager().createQuery(queryString, entityClass);
		if (queryLimit != null) {
			typedQuery.setFirstResult(queryLimit.getStart());
			typedQuery.setMaxResults(queryLimit.getCount());
		}
		List<T> resultList = typedQuery.getResultList();
		return resultList;
	}

	/**
	 * Returns a {@link PageDto} of all entities according to the given
	 * <code>basePageContextDto</code>. This method ensures a list of {@link T}
	 * s.
	 * 
	 * @param entityClass The entity class
	 * @param contextDto contains the order by and limit information for the
	 *            query
	 * @return List of entities.
	 */
	public <T> PageDto<T> findAll(Class<T> entityClass, ContextDto contextDto) {
		List<T> list = findAll(entityClass, contextDto.getOrderBy(), contextDto.getLimit());
		int totalNumberOfRecords = countAll(entityClass);
		return new PageDto<T>(list, totalNumberOfRecords);
	}

	/**
	 * Returns a {@link PageDto} of all entities, created by executing the query
	 * and count query in the given <code>pagedQueryProvider</code>. This method
	 * ensures a list of {@link T} s.
	 * 
	 * @param entityClass The entity class
	 * @param pagedQueryProvider provides the query and count query to execute
	 * @return List of entities.
	 */
	public <T> PageDto<T> findPage(Class<T> entityClass, PagedQueryProviderInterface<T> pagedQueryProvider) {
		List<T> list = findAll(entityClass, pagedQueryProvider.getQuery(entityManager));
		int totalNumberOfRecords = countAll(pagedQueryProvider.getCountQuery(entityManager));
		return new PageDto<T>(list, totalNumberOfRecords);
	}

	/**
	 * Returns the count of all entities according to the given entity
	 * <code>entityClass</code>.
	 * 
	 * @param entityClass The entity class
	 * @return Count of all entities.
	 */
	public <T> int countAll(Class<T> entityClass) {
		String queryString = "select count(" + ENTITY_ALIAS + ") from " + entityClass.getSimpleName() + " "
		        + ENTITY_ALIAS;
		TypedQuery<Long> typedQuery = getEntityManager().createQuery(queryString, Long.class);
		return countAll(typedQuery);
	}

	/**
	 * Returns the count of all entities according to the given Java Persistence
	 * query language statement <code>qlString</code>.
	 * 
	 * @param typedQuery The Java Persistence query language statement to
	 *            execute
	 * @return Count of all entities.
	 */
	public int countAll(TypedQuery<Long> typedQuery) {
		int count;
		try {
			count = typedQuery.getSingleResult().intValue();
		} catch (NoResultException e) {
			count = 0;
		}
		return count;
	}

	/**
	 * Returns the number of removed entities.
	 * 
	 * @param entityClass The entity class
	 * @return Number of removed entities.
	 */
	public <T> int removeAll(Class<T> entityClass) {
		String queryString = "delete from " + entityClass.getSimpleName() + " " + ENTITY_ALIAS;
		Query query = getEntityManager().createQuery(queryString);
		return query.executeUpdate();
	}

	// /**
	// * Returns page with entities according to the <code>pageContext</code>.
	// * This method uses the {@link #findAll(PageContext)} and
	// * {@link #countAll(PageContext)}.
	// *
	// * @param pageContext The pageContext.
	// * @return The page with entities.
	// */
	// public Page<T> fetchPage(PageContext pageContext) {
	// Page<T> page = new Page<T>();
	//
	// // Get the total count
	// int totalNumberOfRows = countAll(pageContext);
	// page.setTotalNumberOfRows(totalNumberOfRows);
	//
	// int numberOfPages = 0;
	// if (totalNumberOfRows > 0) {
	// numberOfPages = new Double((Math.floor(totalNumberOfRows /
	// pageContext.getNumberOfRowsInView()))).intValue();
	// if (totalNumberOfRows % pageContext.getNumberOfRowsInView() > 0) {
	// numberOfPages = numberOfPages + 1;
	// }
	// }
	// page.setNumberOfPages(numberOfPages);
	//
	// List<T> resultList = findAll(pageContext);
	// page.setData(resultList);
	//
	// return page;
	// }
	//
	// /**
	// * Returns list with entities according to the <code>pageContext</code>.
	// *
	// * @param pageContext The pageContext.
	// * @return The page with entities.
	// */
	// public List<T> findAll(PageContext pageContext) {
	// // Get the result list.
	// // Note: pageNumber in pageContext starts at 0.
	// // Note: the startPosition for the query begins at 0.
	// TypedQuery<T> query = findAllQuery(pageContext);
	// List<T> resultList = null;
	// int startPosition = pageContext.getPageNumber() <= 0 ? 0 :
	// (pageContext.getPageNumber() * pageContext.getNumberOfRowsInView());
	// resultList = query.setFirstResult(startPosition).
	// setMaxResults(pageContext.getNumberOfRowsInView()).getResultList();
	// return resultList;
	// }
	//
	// /**
	// * Returns the total number of rows, without using paging, according to
	// the
	// * <code>pageContext</code>.
	// *
	// * @param pageContext The pageContext.
	// * @return The total count.
	// */
	// public int countAll(PageContext pageContext) {
	// // Get the total count
	// int totalNumberOfRows = countAll(countAllQuery(pageContext));
	// return totalNumberOfRows;
	// }

	/**
	 * Returns the entity matching the <code>id</code> or null if the entity can
	 * not be found.
	 * 
	 * @param entityClass The entity class
	 * @param id Primary key of the entity.
	 */
	public <T, I extends Serializable> T findById(Class<T> entityClass, I id) {
		T entity = getEntityManager().find(entityClass, id);
		return entity;
	}

	/**
	 * Returns the entity matching the <code>id</code> or JpaException if the
	 * entity can not be found.
	 * 
	 * @param entityClass The entity class
	 * @param id Primary key of the entity.
	 * @return The entity or null if the entity cannot be found.
	 */
	public <T, I extends Serializable> T findByIdRequired(Class<T> entityClass, I id) {
		T entity = getEntityManager().find(entityClass, id);
		if (entity == null) {
			throw new IllegalStateException("entity not found");
		}
		return entity;
	}

	/**
	 * Refresh the entity.
	 * 
	 * @param entity The entity to refresh.
	 */
	public <T> void refresh(T entity) {
		getEntityManager().refresh(entity);
	}

	/**
	 * Save or update an entity. The entity is immediately synchronized to the
	 * database.
	 * 
	 * Using T as type of the parameter entity makes more sense. However it was
	 * not possible to get this to work with the CRUD service so the type was
	 * changed to Object.
	 * 
	 * @param entityClass The entity class
	 * @param entity The entity to persistent.
	 * @return The entity that is persistent.
	 */
	@SuppressWarnings("unchecked")
	public <T> T persist(Class<T> entityClass, Object entity) {
		T typedEntity = (T) entity;
		T e = getEntityManager().merge(typedEntity);
		getEntityManager().flush();
		return e;
	}

	/**
	 * Remove an entity. The entity removal is immediately synchronized to the
	 * database.
	 * 
	 * @param entityClass The entity class
	 * @param entity The entity to remove.
	 */
	public <T> void remove(Class<T> entityClass, T entity) {
		getEntityManager().remove(entity);
		getEntityManager().flush();
	}

	/**
	 * Remove an entity. The entity removal is immediately synchronized to the
	 * database.
	 * 
	 * @param entityClass The entity class
	 * @param id The primary key of the entity to remove.
	 */
	public <T, I extends Serializable> void remove(Class<T> entityClass, I id) {
		T theEntity = getEntityManager().find(entityClass, id);
		getEntityManager().remove(theEntity);
		getEntityManager().flush();
	}

	/**
	 * Flushes all pending database operations.
	 */
	public void flush() {
		getEntityManager().flush();
	}

	/**
	 * Calls clear on the entity manager.
	 */
	public void clear() {
		getEntityManager().clear();
	}

	// /**
	// * Get the Java Persistence query language statement for selecting all
	// * entities filtered according to <code>pageContext</code>. The basic
	// * implementation does not use the pageContext filter and order by .
	// * This is by design. Because, at this moment, there is no simple way
	// * to build a query according to the filter when it needs more entities
	// * in the from clause.
	// * Note: We can let an PageContext implementation return a Query instance
	// but
	// * is this wise.
	// *
	// * It does not make use of the pageContext filter and order by. Override
	// * this method in the subclass to implement the use of the filter and
	// order by.
	// *
	// * @param pageContext The pageContext.
	// * @return The Query.
	// */
	// protected TypedQuery<Entity> findAllQuery(PageContext pageContext) {
	// String query = "select " + ENTITY_IDENTIFICATION +
	// " from " + this.entityType.getSimpleName() + " " + ENTITY_IDENTIFICATION;
	// logger.info("Constructed query: " + query);
	// return entityManager.createQuery(query, this.entityType);
	// }
	//
	// /**
	// * This method is the equivalent from {@link #findAllQuery(PageContext)}
	// but where
	// * the {@link #findAllQuery(PageContext)} returns entities this method
	// returns the
	// * total number of entities found according to the {@link PageContext}.
	// *
	// * It does not make use of the pageContext filter. Override this method in
	// the
	// * subclass to implement the use of the filter.
	// *
	// * @param pageContext The pageContext.
	// * @return The Query.
	// */
	// protected Query countAllQuery(PageContext pageContext) {
	// String query = "select count(" + ENTITY_IDENTIFICATION + ")" +
	// " from " + this.entityType.getSimpleName() + " " + ENTITY_IDENTIFICATION;
	// logger.info("Constructed query: " + query);
	// return entityManager.createQuery(query);
	// }

	// Accessors

	/** Returns the JPQL alias for the entity. */
	static public String getEntityAlias() {
		return ENTITY_ALIAS;
	}

	/**
	 * Returns the entity manager.
	 * 
	 * @return the entity manager
	 */
	public EntityManager getEntityManager() {
		return entityManager;
	}

	/**
	 * Set the entity manager.
	 * 
	 * @param entityManager the entity manager
	 */
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

}
