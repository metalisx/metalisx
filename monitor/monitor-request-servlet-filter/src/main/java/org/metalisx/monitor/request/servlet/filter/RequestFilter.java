package org.metalisx.monitor.request.servlet.filter;

import java.io.IOException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.metalisx.monitor.context.InterfaceMonitorContext;
import org.metalisx.monitor.context.MonitorContextFactory;
import org.metalisx.monitor.context.request.MonitorContextRequestUtils;
import org.metalisx.monitor.domain.model.MonitorRequest;
import org.metalisx.monitor.domain.model.MonitorRequestCertificate;
import org.metalisx.monitor.domain.model.MonitorRequestCookie;
import org.metalisx.monitor.domain.model.MonitorRequestFormParameter;
import org.metalisx.monitor.domain.model.MonitorRequestFormParameterValue;
import org.metalisx.monitor.domain.model.MonitorRequestHeader;
import org.metalisx.monitor.domain.model.MonitorRequestLocale;
import org.metalisx.monitor.domain.model.MonitorRequestPart;
import org.metalisx.monitor.domain.model.MonitorRequestPartHeader;
import org.metalisx.monitor.domain.model.MonitorRequestSecurity;
import org.metalisx.monitor.domain.model.MonitorResponse;
import org.metalisx.monitor.domain.model.MonitorResponseCookie;
import org.metalisx.monitor.domain.model.MonitorResponseHeader;
import org.metalisx.monitor.domain.model.MonitorSession;
import org.metalisx.monitor.domain.service.MonitorRequestService;
import org.metalisx.utils.HttpStatus;
import org.metalisx.utils.HttpUtils;
import org.metalisx.utils.PrettyPrintUtils;


/**
 * Filter for storing request and response information into the database.
 * 
 * @author Stefan Oude Nijhuis
 * 
 */
public class RequestFilter implements Filter {

    @Inject
    private MonitorRequestService requestMonitorService;

    @Inject
    private RequestFilterContext requestFilterContext;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
    	InterfaceMonitorContext monitorContext = MonitorContextRequestUtils.initMonitorContext(RequestFilter.class.getName(), request);
        try {
            if (request instanceof HttpServletRequest && !requestFilterContext.isDisableFilter()) {
                TeeHttpServletRequestWrapper teeHttpServletRequestWrapper = new TeeHttpServletRequestWrapper(
                        (HttpServletRequest) request);
                TeeHttpServletResponseWrapper teeHttpServletResponseWrapper = new TeeHttpServletResponseWrapper(
                        (HttpServletResponse) response);
                MonitorRequest monitorRequest = toRequest(monitorContext, teeHttpServletRequestWrapper);
                monitorRequest = requestMonitorService.persist(monitorRequest);
                chain.doFilter(teeHttpServletRequestWrapper, teeHttpServletResponseWrapper);
                if (response instanceof HttpServletResponse) {
                    monitorRequest.setResponse(toResponse(teeHttpServletResponseWrapper));
                }
                monitorRequest.setEndTime(new Date());
                requestMonitorService.persist(monitorRequest);
            } else {
                chain.doFilter(request, response);
            }
        } finally {
            MonitorContextFactory.clear(RequestFilter.class.getName());
        }
    }

    private MonitorRequest toRequest(InterfaceMonitorContext monitorContext, TeeHttpServletRequestWrapper teeHttpServletRequestWrapper) throws IOException,
            ServletException {
        // Asynchronous request
        // AsyncContext asyncContext =
        // teeHttpServletRequestWrapper.getAsyncContext();
        MonitorRequest monitorRequest = new MonitorRequest();
        monitorRequest.setRequestId(monitorContext.getRequestId());
        monitorRequest.setParentRequestId(monitorContext.getParentRequestId());
        monitorRequest.setOrganization(monitorContext.getOrganization());
        monitorRequest.setUsername(monitorContext.getUsername());
        monitorRequest.setUrl(getUrl(teeHttpServletRequestWrapper));
        monitorRequest.setThread(Thread.currentThread().getName());
        monitorRequest.setStartTime(new Date());
        setMonitorRequestUrlParts(monitorRequest, teeHttpServletRequestWrapper);
        setMonitorRequestHeaders(monitorRequest, teeHttpServletRequestWrapper);
        setMonitorRequestCookies(monitorRequest, teeHttpServletRequestWrapper);
        setMonitorRequestLocales(monitorRequest, teeHttpServletRequestWrapper);
        setMonitorRequestSession(monitorRequest, teeHttpServletRequestWrapper);
        setMonitorRequestSecurity(monitorRequest, teeHttpServletRequestWrapper);
        setMonitorRequestCertificates(monitorRequest, teeHttpServletRequestWrapper);
        setMonitorRequestContent(monitorRequest, teeHttpServletRequestWrapper);
        return monitorRequest;
    }

    private void setMonitorRequestUrlParts(MonitorRequest monitorRequest,
            TeeHttpServletRequestWrapper teeHttpServletRequestWrapper) {
        monitorRequest.setMethod(teeHttpServletRequestWrapper.getMethod());
        monitorRequest.setScheme(teeHttpServletRequestWrapper.getScheme());
        monitorRequest.setServerName(teeHttpServletRequestWrapper.getServerName());
        monitorRequest.setServerPort(teeHttpServletRequestWrapper.getServerPort());
        monitorRequest.setContextPath(teeHttpServletRequestWrapper.getContextPath());
        monitorRequest.setServletPath(teeHttpServletRequestWrapper.getServletPath());
        monitorRequest.setPathInfo(teeHttpServletRequestWrapper.getPathInfo());
        monitorRequest.setQueryString(teeHttpServletRequestWrapper.getQueryString());
        monitorRequest.setPathTranslated(teeHttpServletRequestWrapper.getPathTranslated());
    }

    private void setMonitorRequestHeaders(MonitorRequest monitorRequest,
            TeeHttpServletRequestWrapper teeHttpServletRequestWrapper) {
        Enumeration<String> enumerationHeaderNames = teeHttpServletRequestWrapper.getHeaderNames();
        if (enumerationHeaderNames != null) {
            while (enumerationHeaderNames.hasMoreElements()) {
                String name = enumerationHeaderNames.nextElement();
                MonitorRequestHeader monitorRequestHeader = new MonitorRequestHeader();
                monitorRequestHeader.setName(name);
                monitorRequestHeader.setValue(teeHttpServletRequestWrapper.getHeader(name));
                monitorRequest.addHeader(monitorRequestHeader);
            }
        }
    }

    public void setMonitorRequestCookies(MonitorRequest monitorRequest,
            TeeHttpServletRequestWrapper teeHttpServletRequestWrapper) {
        Cookie[] requestCookies = teeHttpServletRequestWrapper.getCookies();
        if (requestCookies != null) {
            for (Cookie requestCookie : requestCookies) {
                MonitorRequestCookie monitorRequestCookie = new MonitorRequestCookie();
                monitorRequestCookie.setName(requestCookie.getName());
                monitorRequestCookie.setValue(requestCookie.getValue());
                monitorRequestCookie.setDomain(requestCookie.getDomain());
                monitorRequestCookie.setMaxAge(requestCookie.getMaxAge());
                monitorRequestCookie.setPath(requestCookie.getPath());
                monitorRequestCookie.setSecure(requestCookie.getSecure());
                monitorRequestCookie.setVersion(requestCookie.getVersion());
                monitorRequest.addCookie(monitorRequestCookie);
            }
        }
    }

    public void setMonitorRequestLocales(MonitorRequest monitorRequest,
            TeeHttpServletRequestWrapper teeHttpServletRequestWrapper) {
        monitorRequest.setLocale(teeHttpServletRequestWrapper.getLocale().getDisplayName());
        Enumeration<Locale> enumerationLocales = teeHttpServletRequestWrapper.getLocales();
        if (enumerationLocales != null) {
            while (enumerationLocales.hasMoreElements()) {
                MonitorRequestLocale monitorRequestLocale = new MonitorRequestLocale();
                monitorRequestLocale.setName(enumerationLocales.nextElement().getDisplayName());
                monitorRequest.addLocale(monitorRequestLocale);
            }
        }
    }

    public void setMonitorRequestSession(MonitorRequest monitorRequest,
            TeeHttpServletRequestWrapper teeHttpServletRequestWrapper) {
        if (teeHttpServletRequestWrapper.getRequestedSessionId() != null) {
            MonitorSession nonitorSession = new MonitorSession();
            nonitorSession.setRequestedSessionId(teeHttpServletRequestWrapper.getRequestedSessionId());
            nonitorSession.setRequestedSessionIdFromCookie(teeHttpServletRequestWrapper
                    .isRequestedSessionIdFromCookie());
            nonitorSession.setRequestedSessionIdFromURL(teeHttpServletRequestWrapper.isRequestedSessionIdFromURL());
            nonitorSession.setRequestedSessionIdValid(teeHttpServletRequestWrapper.isRequestedSessionIdValid());
            monitorRequest.setSession(nonitorSession);
        }
    }

    public void setMonitorRequestSecurity(MonitorRequest monitorRequest,
            TeeHttpServletRequestWrapper teeHttpServletRequestWrapper) {
        MonitorRequestSecurity monitorRequestSecurity = new MonitorRequestSecurity();
        monitorRequestSecurity.setSecure(teeHttpServletRequestWrapper.isSecure());
        monitorRequestSecurity.setAuthType(teeHttpServletRequestWrapper.getAuthType());
        monitorRequestSecurity.setRemoteUser(teeHttpServletRequestWrapper.getRemoteUser());
        if (teeHttpServletRequestWrapper.getUserPrincipal() != null) {
            monitorRequestSecurity.setUserPrincipal(teeHttpServletRequestWrapper.getUserPrincipal().getName());
        }
        monitorRequest.setSecurity(monitorRequestSecurity);
    }

    private void setMonitorRequestCertificates(MonitorRequest monitorRequest,
            TeeHttpServletRequestWrapper teeHttpServletRequestWrapper) {
        X509Certificate[] certificates = (X509Certificate[]) teeHttpServletRequestWrapper
                .getAttribute("javax.servlet.request.X509Certificate");
        if (certificates != null && certificates.length > 0) {
            for (X509Certificate certificate : certificates) {
                MonitorRequestCertificate monitorRequestCertificate = new MonitorRequestCertificate();
                try {
                    certificate.checkValidity();
                    monitorRequestCertificate.setValid(true);
                } catch (CertificateExpiredException e1) {
                    monitorRequestCertificate.setValid(false);
                } catch (CertificateNotYetValidException e1) {
                    monitorRequestCertificate.setValid(false);
                }
                monitorRequestCertificate.setType(certificate.getType());
                if (certificate.getSubjectX500Principal() != null) {
                    monitorRequestCertificate.setSubjectDn(certificate.getSubjectX500Principal().getName());
                }
                if (certificate.getIssuerX500Principal() != null) {
                    monitorRequestCertificate.setIssuerDn(certificate.getIssuerX500Principal().getName());
                }
                try {
                    monitorRequestCertificate.setContent(certificate.getEncoded());
                } catch (CertificateEncodingException e) {
                    e.printStackTrace();
                }
                monitorRequest.addCertificate(monitorRequestCertificate);
            }
        }
    }

    public void setMonitorRequestContent(MonitorRequest monitorRequest,
            TeeHttpServletRequestWrapper teeHttpServletRequestWrapper) throws IOException, ServletException {
        if (HttpUtils.isMultipart(teeHttpServletRequestWrapper)) {
            setMonitorRequestParts(monitorRequest, teeHttpServletRequestWrapper);
        } else if (HttpUtils.isForm(teeHttpServletRequestWrapper)) {
            setMonitorRequestFormParameters(monitorRequest, teeHttpServletRequestWrapper);
        } else {
            monitorRequest.setContent(teeHttpServletRequestWrapper.getInputStreamAsByteArray());
            monitorRequest.setTextContent(HttpUtils.isText(teeHttpServletRequestWrapper.getInputStreamAsByteArray()));
        }
        monitorRequest.setContentLength(teeHttpServletRequestWrapper.getContentLength());
        monitorRequest.setCharacterEncoding(teeHttpServletRequestWrapper.getCharacterEncoding());
        monitorRequest.setContentType(teeHttpServletRequestWrapper.getContentType());
        monitorRequest
                .setPrettyPrint(PrettyPrintUtils.isPrettyPrintable(teeHttpServletRequestWrapper.getContentType()));
    }

    private void setMonitorRequestParts(MonitorRequest monitorRequest,
            TeeHttpServletRequestWrapper teeHttpServletRequestWrapper) throws IOException, ServletException {
        Collection<Part> parts = teeHttpServletRequestWrapper.getParts();
        for (Part part : parts) {
            MonitorRequestPart monitorRequestPart = new MonitorRequestPart();
            monitorRequestPart.setName(part.getName());
            String filename = HttpUtils.getFilename(part);
            if (filename != null) {
                monitorRequestPart.setFilename(filename);
                monitorRequestPart.setContentType(part.getContentType());
                monitorRequestPart.setContentLength(part.getSize());
                byte[] content = HttpUtils.toByteArray(part.getInputStream());
                monitorRequestPart.setContent(content);
                monitorRequestPart.setTextContent(HttpUtils.isText(content));
                monitorRequestPart.setPrettyPrint(PrettyPrintUtils.isPrettyPrintable(part.getContentType()));
            } else {
                monitorRequestPart.setValue(HttpUtils.toString(part.getInputStream(),
                        teeHttpServletRequestWrapper.getCharacterEncoding()));
            }
            Collection<String> headerNames = part.getHeaderNames();
            if (headerNames != null) {
                for (String name : headerNames) {
                    MonitorRequestPartHeader monitorRequestPartHeader = new MonitorRequestPartHeader();
                    monitorRequestPartHeader.setName(name);
                    monitorRequestPartHeader.setValue(part.getHeader(name));
                    monitorRequestPart.addHeader(monitorRequestPartHeader);
                }
            }
            monitorRequest.addPart(monitorRequestPart);
        }
    }

    private void setMonitorRequestFormParameters(MonitorRequest monitorRequest,
            TeeHttpServletRequestWrapper teeHttpServletRequestWrapper) {
        Map<String, String[]> parameterMap = teeHttpServletRequestWrapper.getParameterMap();
        Set<String> keys = parameterMap.keySet();
        for (String formParameterName : keys) {
            MonitorRequestFormParameter monitorRequestFormParameter = new MonitorRequestFormParameter();
            monitorRequestFormParameter.setName(formParameterName);
            String[] values = parameterMap.get(formParameterName);
            for (String value : values) {
                MonitorRequestFormParameterValue monitorRequestFormParameterValue = new MonitorRequestFormParameterValue();
                monitorRequestFormParameterValue.setValue(value);
                monitorRequestFormParameter.addValue(monitorRequestFormParameterValue);
            }
            monitorRequest.addFormParameter(monitorRequestFormParameter);
        }
    }

    private String getUrl(ServletRequest request) {
        StringBuffer url = new StringBuffer();
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            String scheme = httpServletRequest.getScheme(); // http
            String serverName = httpServletRequest.getServerName(); // hostname.com
            int serverPort = httpServletRequest.getServerPort(); // 80
            String contextPath = httpServletRequest.getContextPath(); // /mywebapp
            String servletPath = httpServletRequest.getServletPath(); // /servlet/MyServlet
            String pathInfo = httpServletRequest.getPathInfo(); // /a/b;c=123
            String queryString = httpServletRequest.getQueryString(); // d=789
            // Reconstruct original requesting URL
            url.append(scheme).append("://").append(serverName).append(":").append(serverPort).append(contextPath)
                    .append(servletPath);
            if (pathInfo != null) {
                url.append(pathInfo);
            }
            if (queryString != null) {
                url.append("?").append(queryString);
            }
        }
        return url.toString();
    }

    private MonitorResponse toResponse(TeeHttpServletResponseWrapper teeHttpServletResponseWrapper) throws IOException {
        MonitorResponse monitorResponse = new MonitorResponse();
        monitorResponse.setCharacterEncoding(teeHttpServletResponseWrapper.getCharacterEncoding());
        monitorResponse.setContentType(teeHttpServletResponseWrapper.getContentType());
        monitorResponse.setPrettyPrint(PrettyPrintUtils.isPrettyPrintable(teeHttpServletResponseWrapper
                .getContentType()));
        monitorResponse.setStatus(teeHttpServletResponseWrapper.getStatus());
        HttpStatus httpStatus = HttpStatus.getValue(teeHttpServletResponseWrapper.getStatus());
        if (httpStatus != null) {
            monitorResponse.setStatusDescription(httpStatus.getDescription());
        }
        monitorResponse.setLocale(teeHttpServletResponseWrapper.getLocale().getDisplayName());
        monitorResponse.setContent(teeHttpServletResponseWrapper.getOutputStreamAsByteArray());
        monitorResponse.setTextContent(HttpUtils.isText(teeHttpServletResponseWrapper.getOutputStreamAsByteArray()));
        setMonitorResponseHeaders(monitorResponse, teeHttpServletResponseWrapper);
        setMonitorResponseCookies(monitorResponse, teeHttpServletResponseWrapper);
        return monitorResponse;
    }

    private void setMonitorResponseHeaders(MonitorResponse monitorResponse,
            TeeHttpServletResponseWrapper teeHttpServletResponseWrapper) {
        Collection<String> enumerationHeaderNames = teeHttpServletResponseWrapper.getHeaderNames();
        if (enumerationHeaderNames != null) {
            for (String headerName : enumerationHeaderNames) {
                MonitorResponseHeader monitorResponseHeader = new MonitorResponseHeader();
                monitorResponseHeader.setName(headerName);
                monitorResponseHeader.setValue(teeHttpServletResponseWrapper.getHeader(headerName));
                monitorResponse.addHeader(monitorResponseHeader);
                // If the content type is null we check if there is a content type header. 
                // If so then the prettyPrint is set according to this content type.
				if (monitorResponse.getContentType() == null
						&& "content-type".equals(headerName.toLowerCase())) {
					monitorResponse.setPrettyPrint(PrettyPrintUtils.isPrettyPrintable(teeHttpServletResponseWrapper
							.getHeader(headerName)));
				}
            }
        }
    }

    private void setMonitorResponseCookies(MonitorResponse monitorResponse,
            TeeHttpServletResponseWrapper teeHttpServletResponseWrapper) {
        Cookie[] requestCookies = teeHttpServletResponseWrapper.getCookies();
        if (requestCookies != null) {
            for (Cookie requestCookie : requestCookies) {
                MonitorResponseCookie monitorResponseCookie = new MonitorResponseCookie();
                monitorResponseCookie.setName(requestCookie.getName());
                monitorResponseCookie.setValue(requestCookie.getValue());
                monitorResponseCookie.setDomain(requestCookie.getDomain());
                monitorResponseCookie.setMaxAge(requestCookie.getMaxAge());
                monitorResponseCookie.setPath(requestCookie.getPath());
                monitorResponseCookie.setSecure(requestCookie.getSecure());
                monitorResponseCookie.setVersion(requestCookie.getVersion());
                monitorResponse.addCookie(monitorResponseCookie);
            }
        }
    }

}
