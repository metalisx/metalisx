package org.metalisx.common.rest.service;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.metalisx.common.domain.dao.AbstractDao;
import org.metalisx.common.domain.dto.ContextDto;
import org.metalisx.common.domain.dto.OrderBy;
import org.metalisx.common.domain.dto.PageDto;
import org.metalisx.common.gson.RestGsonConverter;
import org.metalisx.common.rest.dto.entity.EntityFieldDto;
import org.metalisx.common.rest.dto.entity.PageMetadataDto;
import org.metalisx.common.rest.metadata.MetadataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract generic rest service.
 * 
 * Unfortunately the setEntityClass method is required for providing the typed class, 
 * because the REST implementation makes it impossible to retrieve the class from 
 * the generic type.
 * 
 * Simple use case of this class:
 * <ul>
 * <li>create a sub class for this class</li>
 * <li>set the generic type</li>
 * <li>annotate it with @Stateless</li>
 * <li>annotate it with @path("/crud"), replace crud if you need</li>
 * <li>inject the correct entity manager with @PersistenceContext</li>
 * <li>create a method and annotate it with @PostConstruct</li>
 * <li>in this method call setEntityManager with the injected entity manager</li>
 * <li>in this method call setEntityClass with the class it manages, this is the generic type</li>
 * <li>create a sub class for the javax.ws.rs.core.Application class</li>
 * <li>annotate it with @ApplicationPath("/rest") and replace rest if you need</li>
 * <ul>
 * 
 */
public abstract class AbstractGenericRestService<T> {

	private static final Logger logger = LoggerFactory.getLogger(AbstractGenericRestService.class);

	@Inject
	private MetadataProvider metadataProvider;
	
	@Inject
	private RestGsonConverter restGsonConverter;

	private Class<T> entityClass;

	private AbstractDao abstractDao = new AbstractDao() {
	};

	public AbstractGenericRestService() {
		logger.debug("Initalized generic rest service " + this.getClass().getName());		
		/*
		This is not working, the setEntityClass needs to be called for now.
		Kept the code to enable when REST implementations can handle this kind of class setup.
		When activating add @SuppressWarnings("unchecked") above the constructor.
		Type type = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		if (type instanceof Class) {
			System.out.println("is class");
			this.entityClass = (Class<T>) type;
		} else if (type instanceof ParameterizedType) {
			System.out.println("is parameterizedTYpe");
			this.entityClass = (Class<T>) ((ParameterizedType) type).getRawType();
		}
		*/
	}

	@GET
	@Path("/metadata")
	@Produces("application/json")
	public List<EntityFieldDto> metadata() throws ClassNotFoundException,
	        InstantiationException, IllegalAccessException {
		return metadataProvider.getEntityMetadata(entityClass);
	}

	@GET
	@Path("/page/metadata")
	@Produces("application/json")
	public PageMetadataDto pageMetadata() throws ClassNotFoundException,
	        InstantiationException, IllegalAccessException {
		return metadataProvider.getPageMetadata(entityClass);
	}

	@POST
	@Path("/page")
	@Consumes("application/json")
	@Produces("application/json")
	public PageDto<T> getPage(ContextDto contextDto)
	        throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		return abstractDao.findAll(entityClass, contextDto);
	}

	@POST
	@Path("/{entityClass}/list")
	@Consumes("application/json")
	@Produces("application/json")
	public List<T> getList(OrderBy orderBy)
	        throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		return abstractDao.findAll(entityClass, orderBy, null);
	}

	@GET
	@Path("/")
	@Produces("application/json")
	public List<T> get() throws ClassNotFoundException,
	        InstantiationException, IllegalAccessException {
		return abstractDao.findAll(entityClass);
	}

	@GET
	@Path("/new-entity")
	@Produces("application/json")
	public T getNewEntity() throws ClassNotFoundException,
	        InstantiationException, IllegalAccessException {
		return entityClass.newInstance();
	}

	@GET
	@Path("/{id}")
	@Produces("application/json")
	public T get(@PathParam("id") Long id)
	        throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		return abstractDao.findById(entityClass, id);
	}

	@DELETE
	@Path("/{id}")
	@Produces("application/json")
	public Long delete(@PathParam("id") Long id)
	        throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		abstractDao.remove(entityClass, id);
		return id;
	}

	@PUT
	@Path("/")
	@Consumes("application/json")
	@Produces("application/json")
	public T put(String body) throws ClassNotFoundException,
	        InstantiationException, IllegalAccessException {
		return abstractDao.persist(entityClass, restGsonConverter.fromJson(body, entityClass));
	}

	@POST
	@Path("/")
	@Consumes("application/json")
	@Produces("application/json")
	public T post(String body) throws ClassNotFoundException,
	        InstantiationException, IllegalAccessException {
		return abstractDao.persist(entityClass, restGsonConverter.fromJson(body, entityClass));
	}

	public void setEntityManager(EntityManager entityManager) {
		abstractDao.setEntityManager(entityManager);
	}

	public void setEntityClass(Class<T> entityClass) {
		this.entityClass = entityClass;
	}

}
