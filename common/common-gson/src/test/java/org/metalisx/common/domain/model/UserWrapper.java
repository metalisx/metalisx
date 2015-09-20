package org.metalisx.common.domain.model;

public class UserWrapper<T> {

    private T user;

    public UserWrapper(T user) {
    	this.user = user;
    }
    
	public T getUser() {
		return user;
	}

	public void setUser(T user) {
		this.user = user;
	}

}
