package org.metalisx.common.rest.dto;

import java.util.List;

public class ItemsDto {

    private List<?> items;

    public ItemsDto() {
    }

    public ItemsDto(List<?> items) {
    	this.items = items;
    }
    
    public void setItems(List<?> items) {
        this.items = items;
    }

    public List<?> getItems() {
        return items;
    }

}
