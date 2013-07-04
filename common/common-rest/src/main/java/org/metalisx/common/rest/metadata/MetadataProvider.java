package org.metalisx.common.rest.metadata;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.metalisx.common.domain.utils.JpaUtils;
import org.metalisx.common.rest.dto.entity.ColumnDto;
import org.metalisx.common.rest.dto.entity.EntityFieldDto;
import org.metalisx.common.rest.dto.entity.PageMetadataDto;

@Named
public class MetadataProvider {

	@Inject
	private EntityFieldTypeMapper entityFieldTypeMapper;
	
	private MetadataProvider() {
	}

	/**
	 * Returns a list of field definitions. Static properties are not included
	 * in the list.
	 * 
	 * @param entityClass The entity class.
	 * @return List of field definitions.
	 */
	public List<EntityFieldDto> getEntityMetadata(String entityClass) throws ClassNotFoundException,
	        InstantiationException, IllegalAccessException {
		return getEntityMetadata(JpaUtils.toClass(entityClass));
	}

	/**
	 * Returns a list of field definitions. Static properties are not included
	 * in the list.
	 * 
	 * @param entity The entity.
	 * @return List of field definitions.
	 */
	public List<EntityFieldDto> getEntityMetadata(Class<?> entity) throws ClassNotFoundException,
	        InstantiationException, IllegalAccessException {
		List<EntityFieldDto> list = new ArrayList<EntityFieldDto>();
		int i = 0;
		for (Field field : entity.getDeclaredFields()) {
			if (!Modifier.isStatic(field.getModifiers())) {
				list.add(fieldToEntityFieldDto(field, i));
				i++;
			}
		}
		return list;
	}

	/**
	 * Returns a list of field definitions for a new entity. Static properties
	 * are not included in the list. Because it returns metadata for a new
	 * entity the properties annotated with javax.persistence.Id are not
	 * included in the list assuming they will be automatically filled when the
	 * entity is persisted.
	 * 
	 * @param entityClass The entity class.
	 * @return List of field definitions.
	 */
	public List<EntityFieldDto> getNewEntityMetadata(String entityClass) throws ClassNotFoundException,
	        InstantiationException, IllegalAccessException {
		Class<?> entity = JpaUtils.toClass(entityClass);
		return getNewEntityMetadata(entity);
	}

	/**
	 * Returns a list of field definitions for a new entity. Static properties
	 * are not included in the list. Because it returns metadata for a new
	 * entity the properties annotated with javax.persistence.Id are not
	 * included in the list assuming they will be automatically filled when the
	 * entity is persisted.
	 * 
	 * @param entity The entity.
	 * @return List of field definitions.
	 */
	public List<EntityFieldDto> getNewEntityMetadata(Class<?> entity) throws ClassNotFoundException,
	        InstantiationException, IllegalAccessException {
		List<EntityFieldDto> list = new ArrayList<EntityFieldDto>();
		int i = 0;
		for (Field field : entity.getDeclaredFields()) {
			if (!Modifier.isStatic(field.getModifiers())) {
				if (!field.isAnnotationPresent(Id.class)) {
					list.add(fieldToEntityFieldDto(field, i));
					i++;
				}
			}
		}
		return list;
	}

	/**
	 * Returns the metadata for a page.
	 *  
	 * @param entityClass The entity class
	 * @return Page metadata
	 */
	public PageMetadataDto getPageMetadata(String entityClass) throws ClassNotFoundException,
	        InstantiationException, IllegalAccessException {
		Class<?> entity = JpaUtils.toClass(entityClass);
		return getPageMetadata(entity);
	}

	/**
	 * Returns the metadata for a page.
	 *  
	 * @param entity The entity
	 * @return Page metadata
	 */
	public PageMetadataDto getPageMetadata(Class<?> entity) throws ClassNotFoundException,
	        InstantiationException, IllegalAccessException {
		PageMetadataDto pageMetadataDto = new PageMetadataDto();
		pageMetadataDto.setSorting(getSorting(entity));
		pageMetadataDto.setColumns(getColumnDtos(entity));
		return pageMetadataDto;
	}

	private EntityFieldDto fieldToEntityFieldDto(Field field, int index) {
		EntityFieldDto entityFieldDto = new EntityFieldDto();
		entityFieldDto.setIndex(index);
		entityFieldDto.setName(field.getName());
		entityFieldDto.setType(entityFieldTypeMapper.getGuiType(field));
		if (field.isAnnotationPresent(Id.class)) {
			entityFieldDto.setPrimaryKey(true);
		}
		if (field.isAnnotationPresent(NotNull.class)) {
			entityFieldDto.setRequired(true);
		}
		return entityFieldDto;
	}

	private List<List<String>> getSorting(Class<?> entity) {
		List<List<String>> sorting = new ArrayList<List<String>>();
		if (entity.getDeclaredFields().length > 0) {
			int sortColumn = 0;
			if (entity.getDeclaredFields().length > 1) {
				sortColumn = 1;
			}
			List<String> sort = new ArrayList<String>();
			sort.add(String.valueOf(sortColumn));
			sort.add("ASC");
			sorting.add(sort);
		}
		return sorting;
	}

	private List<ColumnDto> getColumnDtos(Class<?> entity) throws ClassNotFoundException,
	        InstantiationException, IllegalAccessException {
		List<ColumnDto> columnDtos = new ArrayList<ColumnDto>();
		for (EntityFieldDto entityFieldDto : getEntityMetadata(entity)) {
			columnDtos.add(fieldToColumnDto(entityFieldDto));
		}
		return columnDtos;
	}

	private ColumnDto fieldToColumnDto(EntityFieldDto entityFieldDto) {
		ColumnDto columnDto = new ColumnDto();
		columnDto.setField(entityFieldDto);
		columnDto.setTitle(entityFieldDto.getName());
		columnDto.setClassName(entityFieldDto.getName());
		columnDto.setSortable(true);
		return columnDto;
	}

}
