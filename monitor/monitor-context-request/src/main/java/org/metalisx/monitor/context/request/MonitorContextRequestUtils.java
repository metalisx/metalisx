package org.metalisx.monitor.context.request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.metalisx.monitor.context.InterfaceMonitorContext;
import org.metalisx.monitor.context.MonitorContextFactory;
import org.metalisx.monitor.context.MonitorContextSimple;

public class MonitorContextRequestUtils {

	public static final String REQUEST_HEADER_REQUEST_ID = "metalisx-monitorcontext-requestid";
	public static final String REQUEST_HEADER_DEPTH = "metalisx-monitorcontext-depth";
	public static final String REQUEST_HEADER_ORGANIZATION = "metalisx-monitorcontext-organization";
	public static final String REQUEST_HEADER_USERNAME = "metalisx-monitorcontext-username";

	private MonitorContextRequestUtils() {
	}

	/**
	 * Returns an initialized {@link MonitorContext}. If there is already one
	 * created it will be returned. If not then a new instance is created with
	 * the <code>key</code> and session id of the <code>request</code>. If there
	 * is a REQUEST_HEADER_REQUEST_ID header on the <code>request</code> the
	 * request contains information on the parent request. This parent request
	 * information is processed. The REQUEST_HEADER_REQUEST_ID value is set as
	 * the parent request id. If the REQUEST_HEADER_DEPTH header exists the
	 * depth is set to this value and increased with 1. If the
	 * REQUEST_HEADER_ORGANIZATION header exists the organization is set to this
	 * value. If the REQUEST_HEADER_USERNAME header exists the username is set
	 * to this value.
	 * 
	 * @param key The key of a new created {@link MonitorContext} instance.
	 * @param request The request.
	 * @return The already created {@link MonitorContext} instance or a new
	 *         {@link MonitorContext} instance.
	 */
	public static InterfaceMonitorContext initMonitorContext(String key, ServletRequest request) {
		InterfaceMonitorContext monitorContext = MonitorContextFactory.getCurrentInstance();
		if (monitorContext == null) {
			monitorContext = MonitorContextFactory.getInstance(key);
			monitorContext.setSessionId(request instanceof HttpServletRequest ? ((HttpServletRequest) request)
			        .getSession().getId() : null);
			InterfaceMonitorContext parentMonitorContext = getParentMonitorContext(request);
			monitorContext = mergeMonitorContextWithParentMonitorContext(monitorContext, parentMonitorContext);
		}
		return monitorContext;
	}

	private static InterfaceMonitorContext mergeMonitorContextWithParentMonitorContext(
	        InterfaceMonitorContext monitorContext, InterfaceMonitorContext parentMonitorContext) {
		if (parentMonitorContext != null) {
			monitorContext.setParentRequestId(parentMonitorContext.getRequestId());
			if (parentMonitorContext.getOrganization() != null) {
				monitorContext.setOrganization(parentMonitorContext.getOrganization());
			}
			if (parentMonitorContext.getUsername() != null) {
				monitorContext.setUsername(parentMonitorContext.getUsername());
			}
			if (parentMonitorContext.getDepth() != null) {
				monitorContext.setDepth(parentMonitorContext.getDepth() + 1);
			}
		}
		return monitorContext;
	}

	private static InterfaceMonitorContext getParentMonitorContext(ServletRequest request) {
		InterfaceMonitorContext monitorContextSimple = null;
		if (request instanceof HttpServletRequest) {
			HttpServletRequest httpServletRequest = (HttpServletRequest) request;
			if (httpServletRequest.getHeader(REQUEST_HEADER_REQUEST_ID) != null) {
				monitorContextSimple = new MonitorContextSimple();
				monitorContextSimple.setRequestId((String) httpServletRequest.getHeader(REQUEST_HEADER_REQUEST_ID));
				monitorContextSimple
				        .setOrganization((String) httpServletRequest.getHeader(REQUEST_HEADER_ORGANIZATION));
				monitorContextSimple.setUsername((String) httpServletRequest.getHeader(REQUEST_HEADER_USERNAME));
				Object depth = httpServletRequest.getHeader(REQUEST_HEADER_DEPTH);
				if (depth != null) {
					monitorContextSimple.setDepth((Integer.valueOf((String) depth)));
				}
			}
		}
		return monitorContextSimple;
	}

	/**
	 * Returns a map with the MonitorContext. Can be used to set on a request.
	 * 
	 * Example on how to Use it in a JAX-RS client: TestWebserviceInterface
	 * client = testWebservice.getPort(TestWebserviceInterface.class);
	 * BindingProvider bindingProvider = (BindingProvider) client;
	 * bindingProvider
	 * .getRequestContext().put(MessageContext.HTTP_REQUEST_HEADERS,
	 * MonitorContextRequestUtils.monitorContextToHeaders());
	 */
	public static Map<String, List<String>> monitorContextToHttpHeaders() {
		Map<String, List<String>> headers = new HashMap<String, List<String>>();
		InterfaceMonitorContext monitorContext = MonitorContextFactory.getCurrentInstance();
		if (monitorContext != null) {
			if (monitorContext.getRequestId() != null) {
				headers.put(REQUEST_HEADER_REQUEST_ID, toHttpHeaderValue(monitorContext.getRequestId()));
			}
			if (monitorContext.getOrganization() != null) {
				headers.put(REQUEST_HEADER_ORGANIZATION, toHttpHeaderValue(monitorContext.getOrganization()));
			}
			if (monitorContext.getUsername() != null) {
				headers.put(REQUEST_HEADER_USERNAME, toHttpHeaderValue(monitorContext.getUsername()));
			}
			if (monitorContext.getDepth() != null) {
				headers.put(REQUEST_HEADER_DEPTH, toHttpHeaderValue(String.valueOf(monitorContext.getDepth())));
			}
		}
		return headers;
	}

	private static List<String> toHttpHeaderValue(String value) {
		List<String> headerValue = new ArrayList<String>();
		headerValue.add(value);
		return headerValue;
	}

	public static void setMonitorContext(ServletRequest request, InterfaceMonitorContext monitorContext) {
		if (monitorContext.getRequestId() != null) {
			request.setAttribute(REQUEST_HEADER_REQUEST_ID, monitorContext.getRequestId());
		}
		if (monitorContext.getOrganization() != null) {
			request.setAttribute(REQUEST_HEADER_ORGANIZATION, monitorContext.getOrganization());
		}
		if (monitorContext.getUsername() != null) {
			request.setAttribute(REQUEST_HEADER_USERNAME, monitorContext.getUsername());
		}
		if (monitorContext.getDepth() != null) {
			request.setAttribute(REQUEST_HEADER_DEPTH, monitorContext.getDepth());
		}
	}

}
