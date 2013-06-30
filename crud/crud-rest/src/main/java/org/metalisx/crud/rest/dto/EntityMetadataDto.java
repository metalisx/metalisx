package org.metalisx.crud.rest.dto;

import java.util.List;

public class EntityMetadataDto {

	private List<EntityFieldDto> fields;
	
	public EntityMetadataDto(List<EntityFieldDto> fields) {
		this.fields = fields;
	}

	public List<EntityFieldDto> getFields() {
		return fields;
	}

}
