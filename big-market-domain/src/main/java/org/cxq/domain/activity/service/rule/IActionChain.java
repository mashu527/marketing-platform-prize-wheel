package org.cxq.domain.activity.service.rule;


import org.cxq.domain.activity.model.entity.ActivityCountEntity;
import org.cxq.domain.activity.model.entity.ActivityEntity;
import org.cxq.domain.activity.model.entity.ActivitySkuEntity;

/**
 * 下单规格过滤
 */
public interface IActionChain extends IActionChainArmory {
    boolean action(ActivitySkuEntity activitySkuEntity, ActivityEntity activityEntity,ActivityCountEntity activityCountEntity);

}
