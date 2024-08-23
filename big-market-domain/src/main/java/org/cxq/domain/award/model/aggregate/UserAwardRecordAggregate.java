package org.cxq.domain.award.model.aggregate;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.cxq.domain.award.model.entity.TaskEntity;
import org.cxq.domain.award.model.entity.UserAwardRecordEntity;

/**
 * 用户中将记录聚合对象 [聚合代表一个事务操作]
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAwardRecordAggregate {

    private UserAwardRecordEntity userAwardRecordEntity;
    private TaskEntity taskEntity;

}
