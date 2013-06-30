package org.metalisx.monitor.context;

/**
 * Interface for the monitor context information.
 * 
 * @author Stefan.Oude.Nijhuis
 * 
 */
public interface InterfaceMonitorContext {

	void clear();

	String format(String message);

	String profileFormat(String message, long duration);

	void increaseDepth();

	void decreaseDepth();

	String getKey();

	void setKey(String key);

	String getSessionId();

	void setSessionId(String sessionId);

	String getRequestId();

	void setRequestId(String requestId);

	String getParentRequestId();

	void setParentRequestId(String parentRequestId);

	String getOrganization();

	void setOrganization(String organization);

	String getUsername();

	void setUsername(String username);

	Integer getDepth();

	void setDepth(Integer depth);

	Boolean getDisableLogging();

	void setDisableLogging(Boolean disableLogging);

}