package org.cxq.domain.activity.service.armory;

import lombok.extern.slf4j.Slf4j;
import org.cxq.domain.activity.model.entity.ActivitySkuEntity;
import org.cxq.domain.activity.repository.IActivityRepository;
import org.cxq.types.common.Constants;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;


@Slf4j
@Service
public class ActivityArmory implements IActivityArmory,IActivityDispatch{
    @Resource
    private IActivityRepository iActivityRepository;

    @Override
    public boolean assembleActivity(Long sku) {
        ActivitySkuEntity activitySkuEntity = iActivityRepository.queryActivitySku(sku);
        cacheActivitySkuStockCount(sku,activitySkuEntity.getStockCount());

        //活动预热【查询时预热到缓存中】
        iActivityRepository.queryRaffleActivityByActivityId(activitySkuEntity.getActivityId());
        //活动次数预热【查询时预热到缓存中】
        iActivityRepository.queryRaffleActivityCountByActivityCountId(activitySkuEntity.getActivityCountId());


        return true;
    }

    private void cacheActivitySkuStockCount(Long sku, Integer stockCount) {
        String cacheKey=Constants.RedisKey.ACTIVITY_SKU_STOCK_COUNT_KEY+sku;
        iActivityRepository.cacheActivitySkuStockCount(cacheKey,stockCount);
    }

    @Override
    public boolean subtractionActivitySkuStock(Long sku, Date endDateTime) {
        String cacheKey=Constants.RedisKey.ACTIVITY_SKU_STOCK_COUNT_KEY+sku;
        return iActivityRepository.subtractionActivitySkuStock(sku,cacheKey,endDateTime);
    }
}
