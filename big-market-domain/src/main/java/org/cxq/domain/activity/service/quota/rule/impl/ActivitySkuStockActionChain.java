package org.cxq.domain.activity.service.quota.rule.impl;

import lombok.extern.slf4j.Slf4j;
import org.cxq.domain.activity.model.entity.ActivityCountEntity;
import org.cxq.domain.activity.model.entity.ActivityEntity;
import org.cxq.domain.activity.model.entity.ActivitySkuEntity;
import org.cxq.domain.activity.model.valobj.ActivitySkuStockKeyVO;
import org.cxq.domain.activity.repository.IActivityRepository;
import org.cxq.domain.activity.service.armory.IActivityDispatch;
import org.cxq.domain.activity.service.quota.rule.AbstractActionChain;
import org.cxq.types.enums.ResponseCode;
import org.cxq.types.exception.AppException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


/**
 *
 * @description 商品库存规则节点
 * @create 2024-03-23 10:25
 */
@Slf4j
@Component("activity_sku_stock_action")
public class ActivitySkuStockActionChain extends AbstractActionChain {
    @Resource
    private IActivityRepository iActivityRepository;
    @Resource
    private IActivityDispatch iActivityDispatch;

    @Override
    public boolean action(ActivitySkuEntity activitySkuEntity, ActivityEntity activityEntity, ActivityCountEntity activityCountEntity) {
        log.info("活动责任链-商品库存处理【有效期、状态、库存(sku)】开始。sku:{} activityId:{}", activitySkuEntity.getSku(), activityEntity.getActivityId());

        boolean state = iActivityDispatch.subtractionActivitySkuStock(activitySkuEntity.getSku(), activityEntity.getEndDateTime());

        if(state){
            log.info("活动责任链-商品库存处理【有效期、状态、库存(sku)】成功。sku:{} activityId:{}", activitySkuEntity.getSku(), activityEntity.getActivityId());

            iActivityRepository.activitySkuStockConsumeSendQueue(ActivitySkuStockKeyVO.builder()
                    .sku(activitySkuEntity.getSku())
                    .activityId(activitySkuEntity.getActivityId())
                    .build());

            return true;
        }

        throw new AppException(ResponseCode.ACTIVITY_SKU_STOCK_ERROR.getCode(),ResponseCode.ACTIVITY_SKU_STOCK_ERROR.getInfo());
    }
}
