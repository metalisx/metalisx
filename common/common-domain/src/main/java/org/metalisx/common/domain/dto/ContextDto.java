package org.metalisx.common.domain.dto;

public class ContextDto {

	private OrderBy orderBy;

	private Limit limit = new Limit();

	public ContextDto() {
	}

	public OrderBy getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(OrderBy orderBy) {
		this.orderBy = orderBy;
	}

	public Limit getLimit() {
		return limit;
	}

	public void setLimit(Limit limit) {
		this.limit = limit;
	}

}
