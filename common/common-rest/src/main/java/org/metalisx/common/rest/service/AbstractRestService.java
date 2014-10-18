package org.metalisx.common.rest.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
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
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.apache.tika.config.TikaConfig;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.metalisx.common.domain.dao.AbstractDao;
import org.metalisx.common.domain.dto.ContextDto;
import org.metalisx.common.domain.dto.OrderBy;
import org.metalisx.common.domain.dto.PageDto;
import org.metalisx.common.domain.utils.JpaUtils;
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
 * Abstract rest service.
 * 
 * Simple use case of this class:
 * <ul>
 * <li>create a sub class for this class</li>
 * <li>annotate it with @Stateless</li>
 * <li>annotate it with @path("/crud"), replace crud if you need</li>
 * <li>inject the correct entity manager with @PersistenceContext</li>
 * <li>create a method and annotate it with @PostConstruct</li>
 * <li>in this method call setEntityManager with the injected entity manager</li>
 * <li>in this method add the available entities with full name to the entityClasses property
 * if you want to use the /metadata rest service for getting the available entities</li>
 * <li>create a sub class for the javax.ws.rs.core.Application class</li>
 * <li>annotate it with @ApplicationPath("/rest") and replace rest if you need</li>
 * <ul>
 */
public abstract class AbstractRestService {

	private static final Logger logger = LoggerFactory.getLogger(AbstractRestService.class);

	@Inject
	protected MetadataProvider metadataProvider;
	
	@Inject
	protected RestGsonConverter restGsonConverter;

	protected AbstractDao abstractDao = new AbstractDao() {
	};

	protected List<String> entityClasses = new ArrayList<String>();
	
	public AbstractRestService() {
		logger.debug("Initalized rest service " + this.getClass().getName());
	}

	@GET
	@Path("/metadata")
	@Produces("application/json")
	public List<EntityMetadataDto> metadata() throws ClassNotFoundException,
	        InstantiationException, IllegalAccessException {
		List<EntityMetadataDto> entityMetadataDtos = new ArrayList<EntityMetadataDto>();
		for (String entityClass : entityClasses) {
			entityMetadataDtos.add(new EntityMetadataDto(entityClass,
					metadataProvider.getEntityMetadata(entityClass)));
		}
		return entityMetadataDtos;
	}
	
	/**
	 * Donwload the byte[] in the field of the object of type entityClass identified by id.
	 * 
	 * The Tika detect method does not return the correct mime types for Microsoft documents
	 * like docx. To get the correct mime types the filename should be set on the Metadata 
	 * instance like: metadata.set(Metadata.RESOURCE_NAME_KEY, "mydoc.docx");
	 * But for this to work a strategy should be introduced to store the filename and
	 * to retrieve it in this method.
	 */
    @GET
    @Path("/{entityClass}/download/{field}/{id}")
    public Response download(@PathParam("entityClass") String entityClass, 
    		@PathParam("field") String field, @PathParam("id") Long id) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException, MimeTypeException {
    	Object object = abstractDao.findById(JpaUtils.toClass(entityClass), id);
        if (object != null) {
        	// Get the byte array
        	Class<?> c = object.getClass();
        	Field[] fields = c.getDeclaredFields();
        	byte[] b = null;
        	for( Field f : fields ){
    			if (field.equals(f.getName().toString())) {
    				f.setAccessible(true);
    				b = (byte[]) f.get(object);
    				break;
    			}
        	}
        	// Detect the mime type
            TikaConfig config = TikaConfig.getDefaultConfig();
        	Metadata metadata = new Metadata();
        	MediaType mediaType = null;
        	MimeType mimeType = null;
        	if (b != null) {
        		TikaInputStream inputStream = TikaInputStream.get(new ByteArrayInputStream(b));
	            mediaType = config.getMimeRepository().detect(inputStream, metadata);
	            mimeType = config.getMimeRepository().forName(mediaType.toString());
        	} else {
	            mimeType = config.getMimeRepository().forName("text/plain");
        	}
        	// Build the response
            ResponseBuilder responseBuilder = Response.ok(b);
            responseBuilder.type(mimeType.toString());
            responseBuilder.header("Content-Disposition", "attachment; filename=\"" + 
            		id + mimeType.getExtension() + "\"");
            return responseBuilder.build();
        }
        return Response.status(Status.NOT_FOUND).build();
    }
    
	@GET
	@Path("/{entityClass}/metadata")
	@Produces("application/json")
	public List<EntityFieldDto> metadata(@PathParam("entityClass") String entityClass) throws ClassNotFoundException,
	        InstantiationException, IllegalAccessException {
		return metadataProvider.getEntityMetadata(entityClass);
	}

	@GET
	@Path("/{entityClass}/page/metadata")
	@Produces("application/json")
	public PageMetadataDto pageMetadata(@PathParam("entityClass") String entityClass) throws ClassNotFoundException,
	        InstantiationException, IllegalAccessException {
		return metadataProvider.getPageMetadata(entityClass);
	}

	@POST
	@Path("/{entityClass}/page")
	@Consumes("application/json")
	@Produces("application/json")
	public PageDto<?> getPage(@PathParam("entityClass") String entityClass, ContextDto contextDto)
	        throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		return abstractDao.findAll(JpaUtils.toClass(entityClass), contextDto);
	}

	@POST
	@Path("/{entityClass}/list")
	@Consumes("application/json")
	@Produces("application/json")
	public List<?> getList(@PathParam("entityClass") String entityClass, OrderBy orderBy)
	        throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		return abstractDao.findAll(JpaUtils.toClass(entityClass), orderBy, null);
	}

	@GET
	@Path("/{entityClass}")
	@Produces("application/json")
	public EntitiesDto get(@PathParam("entityClass") String entityClass) throws ClassNotFoundException,
	        InstantiationException, IllegalAccessException {
		List<?> list = abstractDao.findAll(JpaUtils.toClass(entityClass));
		return new EntitiesDto(list, new EntityMetadataDto(entityClass, metadataProvider.getEntityMetadata(entityClass)));
	}

	@GET
	@Path("/{entityClass}/new-entity")
	@Produces("application/json")
	public EntityDto getNewEntity(@PathParam("entityClass") String entityClass) throws ClassNotFoundException,
	        InstantiationException, IllegalAccessException {
		Object entity = JpaUtils.toClass(entityClass).newInstance();
		return new EntityDto(entity, new EntityMetadataDto(entityClass, metadataProvider.getNewEntityMetadata(entityClass)));
	}

	@GET
	@Path("/{entityClass}/{id}")
	@Produces("application/json")
	public EntityDto get(@PathParam("entityClass") String entityClass, @PathParam("id") Long id)
	        throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		return new EntityDto(abstractDao.findById(JpaUtils.toClass(entityClass), id), new EntityMetadataDto(
				entityClass, metadataProvider.getEntityMetadata(entityClass)));
	}

	@DELETE
	@Path("/{entityClass}/{id}")
	@Produces("application/json")
	public Long delete(@PathParam("entityClass") String entityClass, @PathParam("id") Long id)
	        throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		abstractDao.remove(JpaUtils.toClass(entityClass), id);
		return id;
	}

	@PUT
	@Path("/{entityClass}")
	@Consumes("application/json")
	@Produces("application/json")
	public Object put(@PathParam("entityClass") String entityClass, String body) throws ClassNotFoundException,
	        InstantiationException, IllegalAccessException {
		Class<?> clazz = JpaUtils.toClass(entityClass);
		return abstractDao.persist(clazz, restGsonConverter.fromJson(body, clazz));
	}

	@POST
	@Path("/{entityClass}")
	@Consumes("application/json")
	@Produces("application/json")
	public Object post(@PathParam("entityClass") String entityClass, String body) throws ClassNotFoundException,
	        InstantiationException, IllegalAccessException {
		Class<?> clazz = JpaUtils.toClass(entityClass);
		return abstractDao.persist(clazz, restGsonConverter.fromJson(body, clazz));
	}

    public void setEntityManager(EntityManager entityManager) {
		abstractDao.setEntityManager(entityManager);
	}

}
