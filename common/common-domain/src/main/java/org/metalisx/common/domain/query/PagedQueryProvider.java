package org.metalisx.common.domain.query;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.metalisx.common.domain.dto.PageContextDto;
import org.metalisx.common.domain.filter.AbstractFilter;

public abstract class PagedQueryProvider<E, F extends AbstractFilter> 
	implements PagedQueryProviderInterface<E> {

	private PageContextDto<F> pageContextDto;
	
	public PagedQueryProvider(PageContextDto<F> pageContextDto) {
		this.pageContextDto = pageContextDto;
	}

	public abstract TypedQuery<E> toQuery(PageContextDto<F> pageContextDto, EntityManager entityManager);
	
	public abstract TypedQuery<Long> toCountQuery(PageContextDto<F> pageContextDto, EntityManager entityManager);

	public TypedQuery<E> getQuery(EntityManager entityManager) {
		return toQuery(pageContextDto, entityManager);
	}

	public TypedQuery<Long> getCountQuery(EntityManager entityManager) {
		return toCountQuery(pageContextDto, entityManager);
	}

}
