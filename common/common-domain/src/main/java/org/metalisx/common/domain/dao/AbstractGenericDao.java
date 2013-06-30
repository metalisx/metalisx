package org.metalisx.common.domain.dao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.metalisx.common.domain.dto.ContextDto;
import org.metalisx.common.domain.dto.Limit;
import org.metalisx.common.domain.dto.OrderBy;
import org.metalisx.common.domain.dto.PageDto;
import org.metalisx.common.domain.query.PagedQueryProviderInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generic JPA class for basic CRUD data access operations for an entity.
 * 
 * This class delegates the methods to an instance of {@link AbstractDao}.
 * 
 * @param <T> The type of the entity on which the operations are executed
 * @param <I> The type of the primary key of the entity
 * 
 * @author Stefan Oude Nijhuis
 * 
 */
public abstract class AbstractGenericDao<T, I extends Serializable> {

	private static final Logger logger = LoggerFactory.getLogger(AbstractGenericDao.class);

	private Class<T> entityClass;

	private AbstractDao abstractDao = new AbstractDao() {
	};

	// Constructors

	/**
	 * Creates a default instance and initializes the field <tt>entityType</tt>
	 * with the type argument of the concrete subclass.
	 */
	@SuppressWarnings("unchecked")
	public AbstractGenericDao() {
		logger.debug("Initalized generic DAO " + this.getClass().getName());
		this.entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	// Convenience methods

	/**
	 * Returns a list of all entities according to the Entities named query
	 * <code>namedQuery</code>. This method ensures a list of {@link T}s.
	 * 
	 * @param namedQuery The name of the named query as specified in the entity
	 * @return List of entities.
	 */
	public List<T> findAllByNamedQuery(String namedQuery) {
		return abstractDao.findAllByNamedQuery(entityClass, namedQuery);
	}

	/**
	 * Returns a list of all persistent entities in no specific order and with
	 * no filter.
	 * 
	 * @return List of entities.
	 */
	public List<T> findAll() {
		return abstractDao.findAll(entityClass);
	}

	/**
	 * Returns a list of all entities according to the given Java Persistence
	 * query language statement <code>qlString</code>. This method ensures a
	 * list of {@link T}s.
	 * 
	 * @param qlString The Java Persistence query language statement to execute
	 * @return List of entities.
	 */
	public List<T> findAll(String qlString) {
		return abstractDao.findAll(entityClass, qlString);
	}

	/**
	 * Returns a list of all entities according to the given Java Persistence
	 * typed query <code>typedQuery</code>. This method ensures a list of
	 * {@link T}s.
	 * 
	 * @param typedQuery The Java Persistence typed query to execute
	 * @return List of entities.
	 */
	public List<T> findAll(TypedQuery<T> typedQuery) {
		return abstractDao.findAll(entityClass, typedQuery);
	}

	/**
	 * Returns a list of all entities according to the given order by and limit.
	 * This method ensures a list of {@link T}s.
	 * 
	 * @param queryOrderBy The order by information for the query
	 * @param queryLimit The limit information for the query
	 * @return List of entities.
	 */
	public List<T> findAll(OrderBy queryOrderBy, Limit queryLimit) {
		return abstractDao.findAll(entityClass, queryOrderBy, queryLimit);
	}

	/**
	 * Returns a {@link PageDto} of all entities according to the given
	 * <code>basePageContextDto</code>. This method ensures a list of {@link T}
	 * s.
	 * 
	 * @param contextDto contains the order by and limit information for the
	 *            query
	 * @return List of entities.
	 */
	public PageDto<T> findAll(ContextDto contextDto) {
		return abstractDao.findAll(entityClass, contextDto);
	}

	/**
	 * Returns a {@link PageDto} of all entities, created by executing the query
	 * and count query in the given <code>pagedQueryProvider</code>. This method
	 * ensures a list of {@link T} s.
	 * 
	 * @param pagedQueryProvider provides the query and count query to execute
	 * @return List of entities.
	 */
	public PageDto<T> findPage(PagedQueryProviderInterface<T> pagedQueryProvider) {
		return abstractDao.findPage(entityClass, pagedQueryProvider);
	}

	/**
	 * Returns the count of all entities according to the given entity
	 * <code>entityClass</code>.
	 * 
	 * @return Count of all entities.
	 */
	public int countAll() {
		return abstractDao.countAll(entityClass);
	}

	/**
	 * Returns the count of all entities according to the given Java Persistence
	 * query language statement <code>qlString</code>.
	 * 
	 * @param query The Java Persistence query language statement to execute
	 * @return Count of all entities.
	 */
	public int countAll(TypedQuery<Long> typedQuery) {
		return abstractDao.countAll(typedQuery);
	}

	/**
	 * Returns the number of removed entities.
	 * 
	 * @return Number of removed entities.
	 */
	public int removeAll() {
		return abstractDao.removeAll(entityClass);
	}

	/**
	 * Returns the entity matching the <code>id</code> or null if the entity can
	 * not be found.
	 * 
	 * @param id primary key of the entity.
	 */
	public T findById(I id) {
		return abstractDao.findById(entityClass, id);
	}

	/**
	 * Returns the entity matching the <code>id</code> or JpaException if the
	 * entity can not be found.
	 * 
	 * @param id primary key of the entity.
	 * @return The entity or null if the entity cannot be found.
	 */
	public T findByIdRequired(I id) {
		return abstractDao.findById(entityClass, id);
	}

	/**
	 * @param entity
	 */
	public void refresh(T entity) {
		abstractDao.refresh(entity);
	}

	/**
	 * Save or update an entity. The entity is immediately synchronized to the
	 * database.
	 * 
	 * @param entity The entity to persistent.
	 * @return The entity that is persistent.
	 */
	public T persist(T entity) {
		return abstractDao.persist(entityClass, entity);
	}

	/**
	 * Remove an entity. The entity removal is immediately synchronized to the
	 * database.
	 * 
	 * @param entityClass The entity class
	 * @param entity The entity to remove.
	 */
	public void remove(T entity) {
		abstractDao.remove(entityClass, entity);
	}

	/**
	 * Remove an entity. The entity removal is immediately synchronized to the
	 * database.
	 * 
	 * @param id The primary key of the entity to remove.
	 */
	public void remove(I id) {
		abstractDao.remove(entityClass, id);
	}

	/**
	 * Flushes all pending database operations.
	 */
	public void flush() {
		abstractDao.flush();
	}

	/**
	 * Calls clear on the entity manager.
	 */
	public void clear() {
		abstractDao.clear();
	}

	// Accessors

	/** Returns the JPQL alias for the entity. */
	static public String getEntityAlias() {
		return AbstractEntityDao.getEntityAlias();
	}

	/** Returns the actual entity bean type of this DAO. */
	public Class<T> getEntityBeanType() {
		return entityClass;
	}

	/**
	 * Returns the entity manager.
	 * 
	 * @return the entity manager
	 */
	public EntityManager getEntityManager() {
		return abstractDao.getEntityManager();
	}

	/**
	 * Set the entity manager.
	 * 
	 * @param entityManager the entity manager
	 */
	public void setEntityManager(EntityManager entityManager) {
		abstractDao.setEntityManager(entityManager);
	}

}
