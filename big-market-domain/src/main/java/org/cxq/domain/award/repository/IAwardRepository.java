package org.cxq.domain.award.repository;

import org.cxq.domain.award.model.aggregate.UserAwardRecordAggregate;

public interface IAwardRepository {
    void saveUserAwardRecord(UserAwardRecordAggregate userAwardRecordAggregate);
}
