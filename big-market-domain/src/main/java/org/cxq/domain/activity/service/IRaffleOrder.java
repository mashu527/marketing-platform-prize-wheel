package org.cxq.domain.activity.service;

import org.cxq.domain.activity.model.entity.ActivityOrderEntity;
import org.cxq.domain.activity.model.entity.ActivityShopCartEntity;
import org.cxq.domain.activity.model.entity.SkuRechargeEntity;

/**
 * 抽奖活动订单接口
 */
public interface IRaffleOrder {


    String createRechargeOrder(SkuRechargeEntity skuRechargeEntity);
}
