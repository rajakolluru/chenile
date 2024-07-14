package org.chenile.jpautils.entity;

import jakarta.persistence.*;
import org.chenile.core.context.ContextContainer;
import org.chenile.core.context.HeaderUtils;
import org.chenile.utils.entity.model.ChenileEntity;
import org.chenile.utils.entity.service.IDGenerator;

import java.util.Date;

@MappedSuperclass
public  class BaseJpaEntity implements ChenileEntity {
    @Id @Column(name = "id") public String id;
    public Date createdTime;
    private Date lastModifiedTime;
    private String lastModifiedBy;

    protected String getPrefix(){
        return getClass().getSimpleName();
    }

    public String tenant;
    public String createdBy;

    public String getId(){
        return this.id;
    }

    public void setId(String id){
        this.id = id;
    }

    @Override
    public Date getCreatedTime() {
        return createdTime;
    }

    @Override
    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public Date getLastModifiedTime() {
        return this.lastModifiedTime;
    }

    @Override
    public void setLastModifiedTime(Date lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    @Override
    public String getLastModifiedBy() {
        return this.lastModifiedBy;
    }

    @Override
    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    @Override
    public String getCreatedBy() {
        return this.createdBy;
    }

    @Override
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public Long getVersion() {
        return this.version;
    }

    @Override
    public void setVersion(Long version) {
        this.version = version;
    }
    @Version  public long version;
    @PrePersist
    @PreUpdate
    void initializeIfRequired() {
        ContextContainer contextContainer = ContextContainer.CONTEXT_CONTAINER;
        if (getId() == null) {
            setId( IDGenerator.generateID(getPrefix()));
        }
        if (this.createdTime == null){
            this.createdTime = new Date();
        }

        this.lastModifiedTime = new Date();
        this.lastModifiedBy = contextContainer.get(HeaderUtils.EMPLOYEE_ID_KEY);
        this.createdBy = contextContainer.get(HeaderUtils.EMPLOYEE_ID_KEY);
        this.tenant = contextContainer.get(HeaderUtils.TENANT_ID_KEY);
    }

}
