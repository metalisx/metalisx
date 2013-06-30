package org.metalisx.common.domain.dto;


/**
 * Limit properties for querying {@link Limit} entities.
 * 
 * @author Stefan.Oude.Nijhuis
 * 
 */
public class Limit {

    private int start = 0;

    private int count = 10;

    public Limit() {
    }

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}
