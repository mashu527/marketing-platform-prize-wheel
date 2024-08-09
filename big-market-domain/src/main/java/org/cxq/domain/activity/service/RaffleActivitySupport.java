package org.cxq.domain.activity.service;


import org.cxq.domain.activity.model.entity.ActivityCountEntity;
import org.cxq.domain.activity.model.entity.ActivityEntity;
import org.cxq.domain.activity.model.entity.ActivitySkuEntity;
import org.cxq.domain.activity.repository.IActivityRepository;
import org.cxq.domain.activity.service.rule.factory.DefaultActivityChainFactory;

/**
 * 抽奖活动的支撑类
 */
public class RaffleActivitySupport {
    protected IActivityRepository activityRepository;

    protected DefaultActivityChainFactory defaultActivityChainFactory;
    public RaffleActivitySupport(IActivityRepository activityRepository,DefaultActivityChainFactory defaultActivityChainFactory) {
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
