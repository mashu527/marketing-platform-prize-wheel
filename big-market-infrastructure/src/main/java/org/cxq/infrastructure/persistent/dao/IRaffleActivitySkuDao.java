package org.cxq.infrastructure.persistent.dao;


import org.apache.ibatis.annotations.Mapper;
import org.cxq.domain.activity.model.entity.ActivitySkuEntity;
import org.cxq.infrastructure.persistent.po.RaffleActivitySku;

import java.util.List;

/**
 * 商品Sku
 */
@Mapper
public interface IRaffleActivitySkuDao {
    RaffleActivitySku queryActivitySku(Long sku);

    void updateActivitySkuStock(Long sku);

    void clearActivitySkuStock(Long sku);

    List<ActivitySkuEntity> queryActivitySkuListByActivityId(Long activityId);
}
