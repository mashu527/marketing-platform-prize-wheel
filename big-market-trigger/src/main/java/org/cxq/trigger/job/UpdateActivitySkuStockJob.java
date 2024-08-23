package org.cxq.trigger.job;


import lombok.extern.slf4j.Slf4j;
import org.cxq.domain.activity.model.valobj.ActivitySkuStockKeyVO;
import org.cxq.domain.activity.service.IRaffleActivitySkuStockService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 更新活动sku库存
 */

@Slf4j
@Component()
public class UpdateActivitySkuStockJob {

    @Resource
    private IRaffleActivitySkuStockService iSkuStock;


    @Scheduled(cron = "0/5 * * * * ?")
    public void exec() throws InterruptedException {
        try {
//            log.info("定时任务，更新活动库存【延迟队列获取，减少对数据库的次数获取，不要产生竞争】");
            ActivitySkuStockKeyVO activitySkuStockKeyVO = iSkuStock.takeQueueValue();
            if(activitySkuStockKeyVO==null) return ;
            log.info("定时任务，更新活动sku库存 sku:{} activityId:{}",activitySkuStockKeyVO.getSku(),activitySkuStockKeyVO.getActivityId());
            iSkuStock.updateActivitySkuStock(activitySkuStockKeyVO.getSku());
        } catch (InterruptedException e) {
            log.error("定时任务，更新活动sku库存失败");
        }
    }

}
