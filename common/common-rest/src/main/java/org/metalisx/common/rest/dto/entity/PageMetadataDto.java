package org.metalisx.common.rest.dto.entity;

import java.util.List;

import org.metalisx.common.domain.dto.PageDto;

/**
 * This class does not extend the {@link PageDto} because the metadata is needed
 * before the actual data is retrieved by the jQuery DataTable. Therefore the
 * metadata is retrieved with a separate REST call.
 */
public class PageMetadataDto {

	private List<ColumnDto> columns;

	private List<List<String>> sorting;

	public PageMetadataDto() {
	}

	public List<ColumnDto> getColumns() {
		return columns;
	}

	public void setColumns(List<ColumnDto> columns) {
		this.columns = columns;
	}

	public List<List<String>> getSorting() {
		return sorting;
	}

	public void setSorting(List<List<String>> sorting) {
		this.sorting = sorting;
	}

}
