package com.megthink.gateway.model;

import java.io.Serializable;
import org.springframework.data.domain.Persistable;
import jakarta.persistence.*;

@MappedSuperclass
public abstract class AbstractPersistableEntity<T extends Serializable> 
        implements Persistable<T> {

    @Transient
    private boolean isNew = true;

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    @PostLoad
    @PostPersist
    void markNotNew() {
        this.isNew = false;
    }
}