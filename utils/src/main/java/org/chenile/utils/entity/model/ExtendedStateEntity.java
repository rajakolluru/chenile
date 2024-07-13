package org.chenile.utils.entity.model;

import org.chenile.stm.StateEntity;

import java.util.Date;

public interface ExtendedStateEntity extends StateEntity, ChenileEntity {
    public void setStateEntryTime(Date date);
    public void setSlaLate(int lateTimeInHours);
    public void setSlaTendingLate(int gettingLateTimeInHours);
    public Date getStateEntryTime();
    public int getSlaLate();
    public int getSlaTendingLate();
}
