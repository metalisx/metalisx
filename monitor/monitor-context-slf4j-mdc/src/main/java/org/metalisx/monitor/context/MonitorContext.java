package org.metalisx.monitor.context;

import org.metalisx.monitor.context.MonitorContextSimple;
import org.slf4j.MDC;

/**
 * Extends the SimpleMonitorContext class for exposing the SimpleMonitorContext
 * properties with slf4j MDC. When using this class you need to configure the
 * formatter of the the log framework. In the formatter you need to add the
 * following before the message: [SessionId: %X{sessionid}, RequestId:
 * %X{requestid}, ParentRequestId: %X{parentrequestid}, Organisatie:
 * %X{organization}, Gebruikersnaam: %X{username}] (Depth: %X{depth})
 * 
 * @author Stefan Oude Nijhuis
 * 
 */
public class MonitorContext extends MonitorContextSimple {

	private static final String DEPTH_KEY = "depth";
	private static final String SESSION_ID_KEY = "sessionid";
	private static final String REQUEST_ID_KEY = "requestid";
	private static final String PARENT_REQUEST_ID_KEY = "parentrequestid";
	private static final String ORGANIZATION_KEY = "organization";
	private static final String USERNAME_KEY = "username";

	@Override
	public String format(String message) {
		return formatMessagePart(message);
	}

	@Override
	public String profileFormat(String message, long duration) {
		return formatMessagePart(message) + ", " + formatTimePart(duration);
	}

	@Override
	public void increaseDepth() {
		super.increaseDepth();
		MDC.put(DEPTH_KEY, String.valueOf(getDepth()));
	}

	@Override
	public void decreaseDepth() {
		super.decreaseDepth();
		MDC.put(DEPTH_KEY, String.valueOf(getDepth()));
	}

	@Override
	public void setSessionId(String sessionId) {
		super.setSessionId(sessionId);
		MDC.put(SESSION_ID_KEY, sessionId);
	}

	@Override
	public void setRequestId(String requestId) {
		super.setRequestId(requestId);
		MDC.put(REQUEST_ID_KEY, requestId);
	}

	@Override
	public void setParentRequestId(String parentRequestId) {
		super.setParentRequestId(parentRequestId);
		MDC.put(PARENT_REQUEST_ID_KEY, parentRequestId);
	}

	@Override
	public void setOrganization(String organization) {
		super.setOrganization(organization);
		MDC.put(ORGANIZATION_KEY, organization);
	}

	@Override
	public void setUsername(String username) {
		super.setUsername(username);
		MDC.put(USERNAME_KEY, username);
	}

	@Override
	public void setDepth(Integer depth) {
		super.setDepth(depth);
		MDC.put(DEPTH_KEY, String.valueOf(depth));
	}

	@Override
	public void clear() {
		super.clear();
		MDC.remove(SESSION_ID_KEY);
		MDC.remove(REQUEST_ID_KEY);
		MDC.remove(PARENT_REQUEST_ID_KEY);
		MDC.remove(ORGANIZATION_KEY);
		MDC.remove(USERNAME_KEY);
		MDC.remove(DEPTH_KEY);
	}

}
