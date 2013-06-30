package org.metalisx.monitor.domain.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.metalisx.common.domain.model.AbstractEntity;
import org.metalisx.common.gson.annotation.GsonTransient;


@Entity
public class MonitorRequestFormParameterValue extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String value;

    @GsonTransient
    @ManyToOne
    @JoinColumn(name = "mfe_id")
    private MonitorRequestFormParameter formParameter;

    public MonitorRequestFormParameterValue() {
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public MonitorRequestFormParameter getFormParamter() {
        return formParameter;
    }

    public void setFormParamter(MonitorRequestFormParameter formParamter) {
        this.formParameter = formParamter;
    }

}
