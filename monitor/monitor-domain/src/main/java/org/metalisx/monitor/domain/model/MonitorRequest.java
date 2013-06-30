package org.metalisx.monitor.domain.model;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.metalisx.common.domain.model.AbstractEntity;
import org.metalisx.common.gson.annotation.GsonTransient;

@Entity
public class MonitorRequest extends AbstractEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String thread;
	private String requestId;
	private String parentRequestId;
	private String organization;
	private String username;

	@Column(length = 4000)
	private String url;
	@Temporal(TemporalType.TIMESTAMP)
	private Date startTime;
	@Temporal(TemporalType.TIMESTAMP)
	private Date endTime;
	private long duration;

	private String method;
	private String characterEncoding;
	private String contentType;
	private String locale;

	private String scheme; // http
	private String serverName; // hostname.com
	private int serverPort; // 80
	private String contextPath; // /mywebapp
	private String servletPath; // /servlet/MyServlet
	private String pathInfo; // /a/b;c=123
	@Column(length = 4000)
	private String queryString; // d=789
	private String pathTranslated;

	private int contentLength;

	private int actualContentLength;

	@GsonTransient
	@Lob
	private byte[] content;

	// Boolean indicating if the content is text. True it is text, false it is
	// binary.
	private boolean textContent = false;

	// Boolean indicating if the content can be pretty printed, True it can,
	// false it can not.
	private boolean prettyPrint = false;

	@ManyToOne(cascade = {CascadeType.ALL }, fetch = FetchType.EAGER)
	@JoinColumn(name = "session_id")
	private MonitorSession session;

	@ManyToOne(cascade = {CascadeType.ALL }, fetch = FetchType.EAGER)
	private MonitorRequestSecurity security;

	@OneToMany(mappedBy = "request", cascade = {CascadeType.ALL }, fetch = FetchType.EAGER, orphanRemoval = true)
	private Set<MonitorRequestCookie> cookies = new HashSet<MonitorRequestCookie>();

	@OneToMany(mappedBy = "request", cascade = {CascadeType.ALL }, fetch = FetchType.EAGER, orphanRemoval = true)
	private Set<MonitorRequestHeader> headers = new HashSet<MonitorRequestHeader>();

	@OneToMany(mappedBy = "request", cascade = {CascadeType.ALL }, fetch = FetchType.EAGER, orphanRemoval = true)
	private Set<MonitorRequestLocale> locales = new HashSet<MonitorRequestLocale>();

	@OneToMany(mappedBy = "request", cascade = {CascadeType.ALL }, fetch = FetchType.EAGER, orphanRemoval = true)
	private Set<MonitorRequestFormParameter> formParameters = new HashSet<MonitorRequestFormParameter>();

	@OneToMany(mappedBy = "request", cascade = {CascadeType.ALL }, fetch = FetchType.EAGER, orphanRemoval = true)
	private Set<MonitorRequestPart> parts = new HashSet<MonitorRequestPart>();

	@OneToMany(mappedBy = "request", cascade = {CascadeType.ALL }, fetch = FetchType.EAGER, orphanRemoval = true)
	private Set<MonitorRequestCertificate> certificates = new HashSet<MonitorRequestCertificate>();

	@ManyToOne(cascade = {CascadeType.ALL }, fetch = FetchType.EAGER)
	private MonitorResponse response;

	public MonitorRequest() {
	}

	@PrePersist
	@PreUpdate
	public void setDuration() {
		if (startTime != null && endTime != null) {
			duration = endTime.getTime() - startTime.getTime();
		} else {
			duration = 0;
		}
	}

	public void setContent(byte[] content) throws IOException {
		this.content = content;
		this.actualContentLength = content.length;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getParentRequestId() {
		return parentRequestId;
	}

	public void setParentRequestId(String parentRequestId) {
		this.parentRequestId = parentRequestId;
	}

	public String getScheme() {
		return scheme;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public String getContextPath() {
		return contextPath;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	public String getServletPath() {
		return servletPath;
	}

	public void setServletPath(String servletPath) {
		this.servletPath = servletPath;
	}

	public String getPathInfo() {
		return pathInfo;
	}

	public void setPathInfo(String pathInfo) {
		this.pathInfo = pathInfo;
	}

	public String getQueryString() {
		return queryString;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	public String getCharacterEncoding() {
		return characterEncoding;
	}

	public void setCharacterEncoding(String characterEncoding) {
		this.characterEncoding = characterEncoding;
	}

	public String getContentType() {
		return contentType;
	}

	public MonitorSession getSession() {
		return session;
	}

	public void setSession(MonitorSession session) {
		this.session = session;
	}

	public MonitorRequestSecurity getSecurity() {
		return security;
	}

	public void setSecurity(MonitorRequestSecurity security) {
		this.security = security;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public byte[] getContent() {
		return content;
	}

	public boolean isTextContent() {
		return textContent;
	}

	public void setTextContent(boolean textContent) {
		this.textContent = textContent;
	}

	public boolean isPrettyPrint() {
		return prettyPrint;
	}

	public void setPrettyPrint(boolean prettyPrint) {
		this.prettyPrint = prettyPrint;
	}

	public Set<MonitorRequestHeader> getHeaders() {
		return headers;
	}

	public void addHeader(MonitorRequestHeader header) {
		header.setRequest(this);
		headers.add(header);
	}

	public Set<MonitorRequestCookie> getCookies() {
		return cookies;
	}

	public void addCookie(MonitorRequestCookie cookie) {
		cookie.setRequest(this);
		cookies.add(cookie);
	}

	public Set<MonitorRequestLocale> getLocales() {
		return locales;
	}

	public void addLocale(MonitorRequestLocale locale) {
		locale.setRequest(this);
		locales.add(locale);
	}

	public Set<MonitorRequestFormParameter> getFormParameters() {
		return formParameters;
	}

	public void addFormParameter(MonitorRequestFormParameter formParameter) {
		formParameter.setRequest(this);
		formParameters.add(formParameter);
	}

	public Set<MonitorRequestPart> getParts() {
		return parts;
	}

	public void addPart(MonitorRequestPart part) {
		part.setRequest(this);
		parts.add(part);
	}

	public Set<MonitorRequestCertificate> getCertificates() {
		return certificates;
	}

	public void addCertificate(MonitorRequestCertificate certificate) {
		certificate.setRequest(this);
		certificates.add(certificate);
	}

	public int getContentLength() {
		return contentLength;
	}

	public void setContentLength(int contentLength) {
		this.contentLength = contentLength;
	}

	public int getActualContentLength() {
		return actualContentLength;
	}

	public void setActualContentLength(int actualContentLength) {
		this.actualContentLength = actualContentLength;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getThread() {
		return thread;
	}

	public void setThread(String thread) {
		this.thread = thread;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public long getDuration() {
		return duration;
	}

	public MonitorResponse getResponse() {
		return response;
	}

	public void setResponse(MonitorResponse response) {
		this.response = response;
	}

	public String getPathTranslated() {
		return pathTranslated;
	}

	public void setPathTranslated(String pathTranslated) {
		this.pathTranslated = pathTranslated;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
