package org.cxq.infrastructure.persistent.dao;

import org.apache.ibatis.annotations.Mapper;
import org.cxq.infrastructure.persistent.po.RaffleActivity;


/**
 *  抽奖活动表
 */
@Mapper
public interface IRaffleActivityDao {

    RaffleActivity queryRaffleActivityByActivityId(Long activityId);
}
