package org.cxq.domain.award.service;

import org.cxq.domain.award.model.entity.UserAwardRecordEntity;

/**
 * 奖品服务接口
 */

public interface IAwardService {
    void saveUserAwardRecord(UserAwardRecordEntity userAwardRecordEntity);
}
