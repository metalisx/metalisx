package org.metalisx.common.rest.dto.entity;

import java.util.List;

public class EntityMetadataDto {

	private String entityClass;
	
	private List<EntityFieldDto> fields;
	
	public EntityMetadataDto(String entityClass, List<EntityFieldDto> fields) {
		this.entityClass = entityClass;
		this.fields = fields;
	}

	public List<EntityFieldDto> getFields() {
		return fields;
	}
	
	public String getEntityClass() {
		return entityClass;
	}

}
