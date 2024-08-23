package org.cxq.infrastructure.persistent.dao;

import cn.bugstack.middleware.db.router.annotation.DBRouterStrategy;
import org.apache.ibatis.annotations.Mapper;
import org.cxq.infrastructure.persistent.po.UserAwardRecord;

/**
 *
 * @description 用户中奖记录表
 * @create 2024-04-03 15:57
 */

@Mapper
@DBRouterStrategy(splitTable = true)
public interface IUserAwardRecordDao {
    void insert(UserAwardRecord userAwardRecord);
}
