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
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.metalisx.common.domain.model.AbstractEntity;
import org.metalisx.common.gson.annotation.GsonTransient;


@Entity
public class MonitorRequestPart extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String filename;

    private String contentType;

    private long contentLength;

    private int actualContentLength;

    // Value is set if the part contained a form value.
    private String value;

    // Content is set if the part contained a file.
    @GsonTransient
    @Lob
    private byte[] content;

    // Boolean indicating if the content is text. True it is text, false it is
    // binary.
    private boolean textContent = false;

    // Boolean indicating if the content can be pretty printed, True it can,
    // false it can not.
    private boolean prettyPrint = false;

    @GsonTransient
    @ManyToOne
    @JoinColumn(name = "mrt_id")
    private MonitorRequest request;

    @OneToMany(mappedBy = "part", cascade = { CascadeType.ALL }, fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<MonitorRequestPartHeader> headers = new HashSet<MonitorRequestPartHeader>();

    public MonitorRequestPart() {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getContentType() {
        return contentType;
    }

    public MonitorRequest getRequest() {
        return request;
    }

    public void setRequest(MonitorRequest request) {
        this.request = request;
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

    public long getContentLength() {
        return contentLength;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    public byte[] getContent() {
        return content;
    }

    public int getActualContentLength() {
        return actualContentLength;
    }

    public void setActualContentLength(int actualContentLength) {
        this.actualContentLength = actualContentLength;
    }

    public Set<MonitorRequestPartHeader> getHeaders() {
        return headers;
    }

    public void addHeader(MonitorRequestPartHeader header) {
        header.setPart(this);
        headers.add(header);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
