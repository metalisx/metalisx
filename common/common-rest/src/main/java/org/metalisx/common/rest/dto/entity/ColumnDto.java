package org.metalisx.common.rest.dto.entity;

public class ColumnDto {

	private EntityFieldDto field;
	
	private String title;

	private boolean sortable = true;
	
	private String className;

	public ColumnDto() {
	}

	public EntityFieldDto getField() {
		return field;
	}

	public void setField(EntityFieldDto field) {
		this.field = field;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isSortable() {
		return sortable;
	}

	public void setSortable(boolean sortable) {
		this.sortable = sortable;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

}
