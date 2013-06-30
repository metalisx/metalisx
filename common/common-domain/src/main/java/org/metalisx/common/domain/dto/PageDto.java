package org.metalisx.common.domain.dto;

import java.util.List;

public class PageDto<E> {

    private List<E> items;

    private int totalNumberOfRows;

    public PageDto(List<E> items, int totalNumberOfRows) {
        this.items = items;
        this.totalNumberOfRows = totalNumberOfRows;
    }

	public List<E> getItems() {
		return items;
	}

	public void setItems(List<E> items) {
		this.items = items;
	}

	public int getTotalNumberOfRows() {
		return totalNumberOfRows;
	}

	public void setTotalNumberOfRows(int totalNumberOfRows) {
		this.totalNumberOfRows = totalNumberOfRows;
	}

}
