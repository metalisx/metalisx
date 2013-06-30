package org.metalisx.common.domain.dao;

import java.io.Serializable;

import org.metalisx.common.domain.model.AbstractEntity;

/**
 * Generic JPA class for basic CRUD data access operations for an entity
 * extending the {@link AbstractEntity}.
 * 
 * This class extends {@link AbstractGenericDao}.
 * 
 * @param <T> The type of the entity on which the operations are executed
 * @param <I> The type of the primary key of the entity
 * 
 * @author Stefan Oude Nijhuis
 * 
 */
public abstract class AbstractEntityGenericDao<T extends AbstractEntity, I extends Serializable> extends
        AbstractGenericDao<T, I> {

}
