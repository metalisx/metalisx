package org.metalisx.crud.domain.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.validation.constraints.NotNull;

import org.metalisx.common.domain.model.AbstractEntity;

@Entity
public class TestTextarea extends AbstractEntity {

    private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
	@NotNull
    private String name;

	@Lob
    private String text;
    
    public TestTextarea() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
