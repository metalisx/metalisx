package org.metalisx.monitor.domain.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import org.metalisx.common.domain.model.AbstractEntity;
import org.metalisx.common.gson.annotation.GsonTransient;


@Entity
public class MonitorRequestCertificate extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean valid;

    private String type;

    private String subjectDn;

    private String issuerDn;

    @GsonTransient
    @Lob
    private byte[] content;

    @GsonTransient
    @ManyToOne
    @JoinColumn(name = "mrt_id")
    private MonitorRequest request;

    public MonitorRequestCertificate() {
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubjectDn() {
        return subjectDn;
    }

    public void setSubjectDn(String subjectDn) {
        this.subjectDn = subjectDn;
    }

    public String getIssuerDn() {
        return issuerDn;
    }

    public void setIssuerDn(String issuerDn) {
        this.issuerDn = issuerDn;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public MonitorRequest getRequest() {
        return request;
    }

    public void setRequest(MonitorRequest request) {
        this.request = request;
    }

}
