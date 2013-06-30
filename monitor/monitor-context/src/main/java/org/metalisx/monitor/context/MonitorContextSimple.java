package org.metalisx.monitor.context;

import java.util.Date;
import java.util.UUID;

/**
 * Context object for the duration of a request. It exposes a random generated
 * request id, the session id, the organization, the user and the depth in the
 * hierarchy of monitor log calls.
 * 
 * Best use of the class is to set properties in a servlet filter and clean it
 * when the filter finishes.
 * 
 * @author Stefan Oude Nijhuis
 * 
 */
public class MonitorContextSimple implements InterfaceMonitorContext {

	private String key;
	
	private String sessionId;

	private String requestId;

	private String parentRequestId;

	private String organization;

	private String username;

	private Integer depth;

	private Boolean disableLogging = false;

	public MonitorContextSimple() {
		setRequestId((new Date()).getTime() + '-' + UUID.randomUUID().toString());
		setDepth(1);
	}

	@Override
	public void clear() {
	}

	@Override
	public String format(String message) {
		return formatMonitorContextPart() + " " + formatMessagePart(message);
	}

	@Override
	public String profileFormat(String message, long duration) {
		return formatMonitorContextPart() + " " + formatMessagePart(message) + ", " + formatTimePart(duration);
	}

	protected String formatMonitorContextPart() {
		return String.format(
		        "[SessionId: %s, RequestId: %s, ParentRequestId: %s, Organisatie: %s, Gebruikersnaam: %s] (Depth: %s)",
		        sessionId == null ? "" : sessionId, requestId == null ? "" : requestId, parentRequestId == null ? ""
		                : parentRequestId, organization == null ? "" : organization, username == null ? "" : username,
		        depth == null ? "" : depth);
	}

	protected String formatMessagePart(String message) {
		return message == null ? "" : message;
	}

	protected String formatTimePart(long duration) {
		return "time: " + duration + "ms";
	}

	@Override
	public void increaseDepth() {
		setDepth(depth + 1);
	}

	@Override
	public void decreaseDepth() {
		setDepth(depth - 1);
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public String getSessionId() {
		return sessionId;
	}

	@Override
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	@Override
	public String getRequestId() {
		return requestId;
	}

	@Override
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	@Override
	public String getParentRequestId() {
		return parentRequestId;
	}

	@Override
	public void setParentRequestId(String parentRequestId) {
		this.parentRequestId = parentRequestId;
	}

	@Override
	public String getOrganization() {
		return organization;
	}

	@Override
	public void setOrganization(String organization) {
		this.organization = organization;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public Integer getDepth() {
		return depth;
	}

	@Override
	public void setDepth(Integer depth) {
		this.depth = depth;
	}

	@Override
	public Boolean getDisableLogging() {
		return disableLogging;
	}

	@Override
	public void setDisableLogging(Boolean disableLogging) {
		this.disableLogging = disableLogging;
	}

}
