package org.cxq.domain.activity.model.aggregate;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.cxq.domain.activity.model.entity.ActivityAccountEntity;
import org.cxq.domain.activity.model.entity.ActivityOrderEntity;


/**
 *下单聚合对象
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderAggregate {

    /**
     * 活动订单实体
     */
    private ActivityOrderEntity activityOrderEntity;
    /**
     * 活动账户实体
     */
    private ActivityAccountEntity activityAccountEntity;
}
