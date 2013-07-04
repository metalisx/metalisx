package org.metalisx.common.rest.dto.entity;

import java.util.List;

import org.metalisx.common.rest.dto.ItemsDto;

public class EntitiesDto extends ItemsDto {

	private EntityMetadataDto metadata;

	public EntitiesDto(List<?> items, EntityMetadataDto metadata) {
	    super(items);
	    this.metadata = metadata;
    }

	public EntityMetadataDto getMetadata() {
		return metadata;
	}
	
}
