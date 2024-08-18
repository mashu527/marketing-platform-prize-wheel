package org.cxq.domain.activity.model.entity;

import com.google.errorprone.annotations.NoAllocation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 参与抽奖活动实体对象
 */

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PartakeRaffleActivityEntity {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 活动ID
     */
    private Long activityId;
}
