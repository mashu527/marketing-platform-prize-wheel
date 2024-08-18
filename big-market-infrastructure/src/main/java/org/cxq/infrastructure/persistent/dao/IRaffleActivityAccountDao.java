package org.cxq.infrastructure.persistent.dao;

import cn.bugstack.middleware.db.router.annotation.DBRouter;
import org.apache.ibatis.annotations.Mapper;
import org.cxq.infrastructure.persistent.po.RaffleActivityAccount;

/**
 *
 * @description 抽奖活动账户表
 * @create 2024-03-09 10:05
 */
@Mapper
public interface IRaffleActivityAccountDao {
    int updateAccountQuota(RaffleActivityAccount raffleActivityAccount);

    void insert(RaffleActivityAccount raffleActivityAccount);

    int updateActivityAccountSubstractionQuota(RaffleActivityAccount raffleActivityAccount);

    void updateActivityAccountMonthSurplusImageQuota(RaffleActivityAccount build);

    void updateActivityAccountDaySurplusImageQuota(RaffleActivityAccount build);

    @DBRouter
    RaffleActivityAccount queryActivityAccountByUserId(RaffleActivityAccount raffleActivityAccountReq);
}
