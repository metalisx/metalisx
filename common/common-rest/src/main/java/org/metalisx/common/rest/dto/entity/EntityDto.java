package org.metalisx.common.rest.dto.entity;

import org.metalisx.common.rest.dto.ItemDto;

public class EntityDto extends ItemDto {

	private EntityMetadataDto metadata;
	
	public EntityDto(Object item, EntityMetadataDto metadata) {
	    super(item);
	    this.metadata = metadata;
    }

	public EntityMetadataDto getMetadata() {
		return metadata;
	}

}
