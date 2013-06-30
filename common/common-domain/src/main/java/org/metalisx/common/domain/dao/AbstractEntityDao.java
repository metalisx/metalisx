package org.metalisx.common.domain.dao;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.metalisx.common.domain.dto.ContextDto;
import org.metalisx.common.domain.dto.Limit;
import org.metalisx.common.domain.dto.OrderBy;
import org.metalisx.common.domain.dto.PageDto;
import org.metalisx.common.domain.model.AbstractEntity;
import org.metalisx.common.domain.query.PagedQueryProviderInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JPA class for basic CRUD data access operations for an entity extending the
 * {@link AbstractEntity}.
 * 
 * This class delegates the methods to an instance of {@link AbstractDao}.
 * 
 * @author Stefan Oude Nijhuis
 * 
 */
public abstract class AbstractEntityDao {

	private static final Logger logger = LoggerFactory.getLogger(AbstractEntityDao.class);

	private AbstractDao abstractDao = new AbstractDao() {
	};

	// Construcors

	public AbstractEntityDao() {
		logger.debug("Initalized entity DAO ");
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
	public <T extends AbstractEntity> List<T> findAllByNamedQuery(Class<T> entityClass, String namedQuery) {
		return abstractDao.findAllByNamedQuery(entityClass, namedQuery);
	}

	/**
	 * Returns a list of all persistent entities in no specific order and with
	 * no filter.
	 * 
	 * @param entityClass The entity class
	 * @return List of entities.
	 */
	public <T extends AbstractEntity> List<T> findAll(Class<T> entityClass) {
		return abstractDao.findAll(entityClass);
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
	public <T extends AbstractEntity> List<T> findAll(Class<T> entityClass, String qlString) {
		return abstractDao.findAll(entityClass, qlString);
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
	public <T extends AbstractEntity> List<T> findAll(Class<T> entityClass, TypedQuery<T> typedQuery) {
		return abstractDao.findAll(entityClass, typedQuery);
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
	public <T extends AbstractEntity> List<T> findAll(Class<T> entityClass, OrderBy queryOrderBy, Limit queryLimit) {
		return abstractDao.findAll(entityClass, queryOrderBy, queryLimit);
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
	public <T extends AbstractEntity> PageDto<T> findAll(Class<T> entityClass, ContextDto contextDto) {
		return abstractDao.findAll(entityClass, contextDto);
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
	public <T extends AbstractEntity> PageDto<T> findPage(Class<T> entityClass,
	        PagedQueryProviderInterface<T> pagedQueryProvider) {
		return abstractDao.findPage(entityClass, pagedQueryProvider);
	}

	/**
	 * Returns the count of all entities according to the given entity
	 * <code>entityClass</code>.
	 * 
	 * @param entityClass The entity class
	 * @return Count of all entities.
	 */
	public <T extends AbstractEntity> int countAll(Class<T> entityClass) {
		return abstractDao.countAll(entityClass);
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
		return abstractDao.countAll(typedQuery);
	}

	/**
	 * Returns the number of removed entities.
	 * 
	 * @param entityClass The entity class
	 * @return Number of removed entities.
	 */
	public <T extends AbstractEntity> int removeAll(Class<T> entityClass) {
		return abstractDao.removeAll(entityClass);
	}

	/**
	 * Returns the entity matching the <code>id</code> or null if the entity can
	 * not be found.
	 * 
	 * @param entityClass The entity class
	 * @param id Primary key of the entity.
	 */
	public <T extends AbstractEntity, I extends Serializable> T findById(Class<T> entityClass, I id) {
		return abstractDao.findById(entityClass, id);
	}

	/**
	 * Returns the entity matching the <code>id</code> or JpaException if the
	 * entity can not be found.
	 * 
	 * @param entityClass The entity class
	 * @param id Primary key of the entity.
	 * @return The entity or null if the entity cannot be found.
	 */
	public <T extends AbstractEntity, I extends Serializable> T findByIdRequired(Class<T> entityClass, I id) {
		return abstractDao.findByIdRequired(entityClass, id);
	}

	/**
	 * Refresh the entity.
	 * 
	 * @param entity The entity to refresh.
	 */
	public <T extends AbstractEntity> void refresh(T entity) {
		abstractDao.refresh(entity);
	}

	/**
	 * Save or update an entity. The entity is immediately synchronized to the
	 * database.
	 * 
	 * Using T as type of the parameter entity makes more sense. However it was
	 * not possible to get this to work with the CRUD service so the type was
	 * changed to Object. Now the method checks if the parameter entity extends
	 * {@link AbstractEntity}, if it does not an exception is thrown.
	 * 
	 * @param entityClass The entity class
	 * @param entity The entity to persistent.
	 * @return The entity that is persistent.
	 */
	public <T extends AbstractEntity> T persist(Class<T> entityClass, Object entity) {
		if (!(entity instanceof AbstractEntity)) {
			throw new IllegalStateException("The object to persist should extend AbstractEntity.");
		}
		return abstractDao.persist(entityClass, entity);
	}

	/**
	 * Remove an entity. The entity removal is immediately synchronized to the
	 * database.
	 * 
	 * @param entityClass The entity class
	 * @param entity The entity to remove.
	 */
	public <T> void remove(Class<T> entityClass, T entity) {
		abstractDao.remove(entityClass, entity);
	}

	/**
	 * Remove an entity. The entity removal is immediately synchronized to the
	 * database.
	 * 
	 * @param entityClass The entity class
	 * @param entity The entity to remove.
	 */
	public <T extends AbstractEntity> void remove(Class<T> entityClass, T entity) {
		abstractDao.remove(entityClass, entity.getId());
	}

	/**
	 * Remove an entity. The entity removal is immediately synchronized to the
	 * database.
	 * 
	 * @param entityClass The entity class
	 * @param id The primary key of the entity to remove.
	 */
	public <T extends AbstractEntity, I extends Serializable> void remove(Class<T> entityClass, I id) {
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
		return AbstractDao.getEntityAlias();
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
