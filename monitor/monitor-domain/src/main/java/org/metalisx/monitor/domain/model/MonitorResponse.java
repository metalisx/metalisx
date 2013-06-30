package org.metalisx.monitor.domain.model;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;

import org.metalisx.common.domain.model.AbstractEntity;
import org.metalisx.common.gson.annotation.GsonTransient;


@Entity
public class MonitorResponse extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int status;

    private String statusDescription;

    private String characterEncoding;

    private String contentType;

    private String locale;

    @OneToMany(mappedBy = "response", cascade = { CascadeType.ALL }, fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<MonitorResponseCookie> cookies = new HashSet<MonitorResponseCookie>();

    @OneToMany(mappedBy = "response", cascade = { CascadeType.ALL }, fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<MonitorResponseHeader> headers = new HashSet<MonitorResponseHeader>();

    @GsonTransient
    @Lob
    private byte[] content;

    // Boolean indicating if the content is text. True it is text, false it is
    // binary.
    private boolean textContent = false;

    // Boolean indicating if the content can be pretty printed, True it can,
    // false it can not.
    private boolean prettyPrint = false;

    // Actual size of the content
    private int contentLength = 0;

    public MonitorResponse() {
    }

    public void setContent(byte[] content) throws IOException {
        this.content = content;
        this.contentLength = content.length;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public Set<MonitorResponseCookie> getCookies() {
        return cookies;
    }

    public void addCookie(MonitorResponseCookie cookie) {
        cookie.setResponse(this);
        cookies.add(cookie);
    }

    public Set<MonitorResponseHeader> getHeaders() {
        return headers;
    }

    public void addHeader(MonitorResponseHeader header) {
        header.setResponse(this);
        headers.add(header);
    }

    public int getContentLength() {
        return contentLength;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

}
