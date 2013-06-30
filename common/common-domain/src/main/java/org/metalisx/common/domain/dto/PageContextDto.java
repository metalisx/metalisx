package org.metalisx.common.domain.dto;

import org.metalisx.common.domain.filter.AbstractFilter;

public class PageContextDto<E extends AbstractFilter> extends ContextDto {

	private E filter;
	
	public PageContextDto() {
	}

	public E getFilter() {
		return filter;
	}

	public void setFilter(E filter) {
		this.filter = filter;
	}

}
