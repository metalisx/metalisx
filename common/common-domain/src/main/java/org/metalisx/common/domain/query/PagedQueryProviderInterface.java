package org.metalisx.common.domain.query;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

public interface PagedQueryProviderInterface<E> {

	TypedQuery<E> getQuery(EntityManager entityManager);

	TypedQuery<Long> getCountQuery(EntityManager entityManager);

}
