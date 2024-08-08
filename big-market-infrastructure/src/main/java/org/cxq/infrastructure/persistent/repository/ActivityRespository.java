package org.cxq.infrastructure.persistent.repository;

import com.google.common.cache.Cache;
import org.cxq.domain.activity.model.entity.ActivityCountEntity;
import org.cxq.domain.activity.model.entity.ActivityEntity;
import org.cxq.domain.activity.model.entity.ActivitySkuEntity;
import org.cxq.domain.activity.model.valobj.ActivityStateVO;
import org.cxq.domain.activity.repository.IActivityRepository;
import org.cxq.infrastructure.persistent.dao.IRaffleActivityCountDao;
import org.cxq.infrastructure.persistent.dao.IRaffleActivityDao;
import org.cxq.infrastructure.persistent.dao.IRaffleActivitySkuDao;
import org.cxq.infrastructure.persistent.po.RaffleActivity;
import org.cxq.infrastructure.persistent.po.RaffleActivityCount;
import org.cxq.infrastructure.persistent.po.RaffleActivitySku;
import org.cxq.infrastructure.persistent.redis.IRedisService;
import org.cxq.types.common.Constants;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;


@Repository
public class ActivityRespository implements IActivityRepository {

    @Resource
    private IRaffleActivitySkuDao iRaffleActivitySkuDao;
    @Resource
    private IRaffleActivityDao iRaffleActivityDao;
    @Resource
    private IRedisService iRedisService;
    @Resource
    private IRaffleActivityCountDao iRaffleActivityCountDao;

    @Override
    public ActivitySkuEntity queryActivitySku(Long sku) {
        RaffleActivitySku raffleActivitySku = iRaffleActivitySkuDao.queryActivitySku(sku);
        return ActivitySkuEntity.builder()
                .sku(raffleActivitySku.getSku())
                .activityCountId(raffleActivitySku.getActivityCountId())
                .activityId(raffleActivitySku.getActivityId())
                .stockCount(raffleActivitySku.getStockCount())
                .stockCountSurplus(raffleActivitySku.getStockCountSurplus())
                .build();
    }

    @Override
    public ActivityEntity queryRaffleActivityByActivityId(Long activityId) {
        //先从数据库中查询活动
        String cacheKey=Constants.RedisKey.ACTIVITY_KEY + activityId;
        ActivityEntity activityEntity = iRedisService.getValue(cacheKey);

        if(activityEntity!=null){
            return activityEntity;
        }
        //数据库查询抽奖活动
        RaffleActivity raffleActivity = iRaffleActivityDao.queryRaffleActivityByActivityId(activityId);

        activityEntity=ActivityEntity.builder()
                .activityId(raffleActivity.getActivityId())
                .activityName(raffleActivity.getActivityName())
                .activityDesc(raffleActivity.getActivityDesc())
                .beginDateTime(raffleActivity.getBeginDateTime())
                .endDateTime(raffleActivity.getEndDateTime())
                .strategyId(raffleActivity.getStrategyId())
                .state(ActivityStateVO.valueOf(raffleActivity.getState()))
                .build();
        //保存再redis中
        iRedisService.setValue(cacheKey,activityEntity);
        return  activityEntity;
    }

    @Override
    public ActivityCountEntity queryRaffleActivityCountByActivityCountId(Long activityCountId) {
        // 优先从缓存获取
        String cacheKey = Constants.RedisKey.ACTIVITY_COUNT_KEY + activityCountId;
        ActivityCountEntity activityCountEntity = iRedisService.getValue(cacheKey);
        if (null != activityCountEntity) return activityCountEntity;
        // 从库中获取数据
        RaffleActivityCount raffleActivityCount = iRaffleActivityCountDao.queryRaffleActivityCountByActivityCountId(activityCountId);
        activityCountEntity = ActivityCountEntity.builder()
                .activityCountId(raffleActivityCount.getActivityCountId())
                .totalCount(raffleActivityCount.getTotalCount())
                .dayCount(raffleActivityCount.getDayCount())
                .monthCount(raffleActivityCount.getMonthCount())
                .build();
        iRedisService.setValue(cacheKey, activityCountEntity);
        return activityCountEntity;
    }
}
