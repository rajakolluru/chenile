package org.chenile.utils.entity.model;

import java.util.Date;

public interface ChenileEntity {
    public String getId();

    public void setId(String id);

    public Date getCreatedTime();

    public void setCreatedTime(Date createdTime);

    public Date getLastModifiedTime();

    public void setLastModifiedTime(Date lastModifiedTime);

    public String getLastModifiedBy();

    public void setLastModifiedBy(String lastModifiedBy);

    public String getCreatedBy();

    public void setCreatedBy(String createdBy);

    public Long getVersion();

    public void setVersion(Long version);
}
