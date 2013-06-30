package org.metalisx.monitor.domain.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.metalisx.common.domain.model.AbstractEntity;
import org.metalisx.common.gson.annotation.GsonTransient;


@Entity
public class MonitorRequestFormParameter extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "formParameter", cascade = { CascadeType.ALL }, fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<MonitorRequestFormParameterValue> values = new HashSet<MonitorRequestFormParameterValue>();

    @GsonTransient
    @ManyToOne
    @JoinColumn(name = "mrt_id")
    private MonitorRequest request;

    public MonitorRequestFormParameter() {
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

    public Set<MonitorRequestFormParameterValue> getValues() {
        return values;
    }

    public void addValue(MonitorRequestFormParameterValue value) {
        value.setFormParamter(this);
        values.add(value);
    }

    public MonitorRequest getRequest() {
        return request;
    }

    public void setRequest(MonitorRequest request) {
        this.request = request;
    }

}
