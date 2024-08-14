package org.cxq.domain.activity.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @description 活动sku库存 key 值对象
 * @create 2024-03-30 09:44
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActivitySkuStockKeyVO {

    /** 商品sku */
    private Long sku;
    /** 活动ID */
    private Long activityId;

}
