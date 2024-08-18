package org.cxq.domain.activity.service.quota;


import org.cxq.domain.activity.model.entity.ActivityCountEntity;
import org.cxq.domain.activity.model.entity.ActivityEntity;
import org.cxq.domain.activity.model.entity.ActivitySkuEntity;
import org.cxq.domain.activity.repository.IActivityRepository;
import org.cxq.domain.activity.service.quota.rule.factory.DefaultActivityChainFactory;

/**
 * 抽奖活动的支撑类
 */
public class RaffleActivityAccountQuotaSupport {
    protected IActivityRepository activityRepository;

    protected DefaultActivityChainFactory defaultActivityChainFactory;
    public RaffleActivityAccountQuotaSupport(IActivityRepository activityRepository, DefaultActivityChainFactory defaultActivityChainFactory) {
        this.activityRepository = activityRepository;
        this.defaultActivityChainFactory=defaultActivityChainFactory;
    }


    public ActivitySkuEntity queryActivitySku(Long sku) {
        return activityRepository.queryActivitySku(sku);
    }

    public ActivityEntity queryRaffleActivityByActivityId(Long activityId) {
        return activityRepository.queryRaffleActivityByActivityId(activityId);
    }

    public ActivityCountEntity queryRaffleActivityCountByActivityCountId(Long activityCountId) {
        return activityRepository.queryRaffleActivityCountByActivityCountId(activityCountId);
    }
}
