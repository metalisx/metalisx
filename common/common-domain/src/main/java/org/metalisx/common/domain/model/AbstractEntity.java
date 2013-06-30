package org.metalisx.common.domain.model;

import java.io.Serializable;

/**
 * Base implementation of an entity. All entities are required to implement the
 * getId method which is required to return the unique identifier of the entity.
 * 
 * The equals method uses the getId to check if the current entity equals the
 * entity passed in. If for some reason this implementation of equality is not
 * sufficient then override the method with a correct implementation.
 * 
 * The hashCode uses the unique identifier returned by getId to generate a hash
 * code for the entity. The same as for the equals method, override it if
 * necessary.
 * 
 * This class functions as a marker class for all entities in domain projects.
 * 
 * Note: If the equals method of an object is overridden then the hashCode
 * should also be overridden.
 * 
 * @author Stefan Oude Nijhuis
 */
public abstract class AbstractEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    protected static final int DEFAULT_SEQUENCE_ALLOCATION_SIZE = 1;

    // --------------------------- Template methods ----------------------------

    /**
     * Return the unique identifier of the entity.
     * 
     * @return unique identifier of the entity
     */
    public abstract Long getId();

    /**
     * Set the unique identifier of the entity.
     * 
     * @param id The unique identifier of the entity
     */
    public abstract void setId(Long id);

    // --------------------------- Overriding methods --------------------------

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (getId() == null || object == null || getClass() != object.getClass()) {
            return false;
        }
        AbstractEntity abstractEntity = (AbstractEntity) object;
        if (abstractEntity.getId() == null) {
            return false;
        }
        return getId().compareTo(abstractEntity.getId()) == 0;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        if (getId() == null) {
            return 0; // For bean validation
        }
        return (31 * Long.valueOf(getId()).hashCode());
    }

    // -------------------------- Convenience methods --------------------------

    /**
     * Returns a string representation of the entity with the class name and id.
     * The id is the unique identifier of the entity.
     * 
     * @return a string representation of the entity
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "(id=" + getId() + ")";
    }

}
