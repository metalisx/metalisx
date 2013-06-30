package org.metalisx.monitor.domain.model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

import org.metalisx.common.domain.model.AbstractEntity;
import org.metalisx.monitor.domain.enumeration.MonitorSettingCode;


@Entity
public class MonitorSetting extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Enumerated(EnumType.STRING)
    private MonitorSettingCode code;

    private String value;

    public MonitorSetting() {
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public MonitorSettingCode getCode() {
        return code;
    }

    public void setCode(MonitorSettingCode code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
