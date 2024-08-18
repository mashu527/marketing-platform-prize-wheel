package org.cxq.infrastructure.persistent.dao;

import cn.bugstack.middleware.db.router.annotation.DBRouter;
import org.apache.ibatis.annotations.Mapper;
import org.cxq.infrastructure.persistent.po.RaffleActivityAccountMonth;

/**
 *
 * @description 抽奖活动账户表-月次数
 * @create 2024-04-03 15:57
 */
@Mapper
public interface IRaffleActivityAccountMonthDao {

    @DBRouter
    RaffleActivityAccountMonth queryActivityAccountMonthByUserId(RaffleActivityAccountMonth raffleActivityAccountMonthReq);
    int updateActivityAccountMonthSubtractionQuota(RaffleActivityAccountMonth build);

    void insertActivityAccountMonth(RaffleActivityAccountMonth build);
}
