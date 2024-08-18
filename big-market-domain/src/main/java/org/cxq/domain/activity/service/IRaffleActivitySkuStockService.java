package org.cxq.domain.activity.service;


import org.cxq.domain.activity.model.valobj.ActivitySkuStockKeyVO;

public interface IRaffleActivitySkuStockService {

    ActivitySkuStockKeyVO takeQueueValue() throws InterruptedException;

    void clearQueueValue();

    void updateActivitySkuStock(Long sku);

    void clearActivitySkuStock(Long sku);

}
