package org.cxq.domain.activity.service;

import org.cxq.domain.activity.model.entity.ActivityOrderEntity;
import org.cxq.domain.activity.model.entity.ActivityShopCartEntity;

/**
 * 抽奖活动订单接口
 */
public interface IRaffleOrder {

    /**
     * 以sku创建抽奖活动订单，活动抽奖资格（可消耗的次数）
     * @param activityShopCartEntity 活动sku实体，通过sku领取活动
     * @return
     */
    ActivityOrderEntity createRaffleActivityOrder(ActivityShopCartEntity activityShopCartEntity);
}
