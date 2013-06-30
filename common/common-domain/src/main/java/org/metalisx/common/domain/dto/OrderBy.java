package org.metalisx.common.domain.dto;

/**
 * Order by properties for querying {@link OrderBy} entities.
 * 
 * @author Stefan.Oude.Nijhuis
 * 
 */
public class OrderBy {

    private String columnName;

    private String columnDirection;

    public OrderBy() {
    }

    public OrderBy(String columnName, String columnDirection) {
        this.columnName = columnName;
        this.columnDirection = columnDirection;
    }

    public String getAsString() {
        return columnName + (columnDirection != null ? " " + columnDirection : "");
    }

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getColumnDirection() {
		return columnDirection;
	}

	public void setColumnDirection(String columnDirection) {
		this.columnDirection = columnDirection;
	}
    
}
