package org.metalisx.common.rest.service;

import java.lang.reflect.ParameterizedType;
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
import org.metalisx.common.domain.dto.PageDto;
import org.metalisx.common.gson.RestGsonConverter;
import org.metalisx.common.rest.dto.entity.EntitiesDto;
import org.metalisx.common.rest.dto.entity.EntityDto;
import org.metalisx.common.rest.dto.entity.EntityFieldDto;
import org.metalisx.common.rest.dto.entity.EntityMetadataDto;
import org.metalisx.common.rest.dto.entity.PageMetadataDto;
import org.metalisx.common.rest.metadata.MetadataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract generic rest service.
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
 * <li>create a sub class for the javax.ws.rs.core.Application class</li>
 * <li>annotate it with @ApplicationPath("/rest") and replace rest if you need</li>
 * <ul>
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

	@SuppressWarnings("unchecked")
	public AbstractGenericRestService() {
		logger.debug("Initalized generic rest service " + this.getClass().getName());
		this.entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
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
	public PageDto<?> getPage(ContextDto contextDto)
	        throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		return abstractDao.findAll(entityClass, contextDto);
	}

	@GET
	@Path("/")
	@Produces("application/json")
	public EntitiesDto get() throws ClassNotFoundException,
	        InstantiationException, IllegalAccessException {
		List<?> list = abstractDao.findAll(entityClass);
		return new EntitiesDto(list, new EntityMetadataDto(metadataProvider.getEntityMetadata(entityClass)));
	}

	@GET
	@Path("/new-entity")
	@Produces("application/json")
	public EntityDto getNewEntity() throws ClassNotFoundException,
	        InstantiationException, IllegalAccessException {
		Object entity = entityClass.newInstance();
		return new EntityDto(entity, new EntityMetadataDto(metadataProvider.getNewEntityMetadata(entityClass)));
	}

	@GET
	@Path("/{id}")
	@Produces("application/json")
	public EntityDto get(@PathParam("id") Long id)
	        throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		return new EntityDto(abstractDao.findById(entityClass, id), new EntityMetadataDto(
				metadataProvider.getEntityMetadata(entityClass)));
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
	public Object put(String body) throws ClassNotFoundException,
	        InstantiationException, IllegalAccessException {
		return abstractDao.persist(entityClass, restGsonConverter.fromJson(body, entityClass));
	}

	@POST
	@Path("/")
	@Consumes("application/json")
	@Produces("application/json")
	public Object post(String body) throws ClassNotFoundException,
	        InstantiationException, IllegalAccessException {
		return abstractDao.persist(entityClass, restGsonConverter.fromJson(body, entityClass));
	}

	public void setEntityManager(EntityManager entityManager) {
		abstractDao.setEntityManager(entityManager);
	}

}
