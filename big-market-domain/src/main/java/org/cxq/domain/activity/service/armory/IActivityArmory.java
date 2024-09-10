package org.cxq.domain.activity.service.armory;

public interface IActivityArmory {

    public boolean assembleActivity(Long sku);

    public boolean assembleActivitySkuByActivityId(Long activityId);
}
