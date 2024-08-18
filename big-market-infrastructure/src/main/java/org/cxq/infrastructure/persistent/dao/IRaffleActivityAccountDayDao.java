package org.cxq.infrastructure.persistent.dao;

import cn.bugstack.middleware.db.router.annotation.DBRouter;
import org.apache.ibatis.annotations.Mapper;
import org.cxq.infrastructure.persistent.po.RaffleActivityAccountDay;

/**
 *
 * @description 抽奖活动账户表-日次数
 * @create 2024-04-03 15:56
 */
@Mapper
public interface IRaffleActivityAccountDayDao {

    @DBRouter
    RaffleActivityAccountDay queryActivityAccountDayByUserId(RaffleActivityAccountDay raffleActivityAccountDayReq);
    int updateActivityAccountDaySubtractionQuota(RaffleActivityAccountDay build);

    void insertActivityAccountDay(RaffleActivityAccountDay build);
}
