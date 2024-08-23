package org.cxq.trigger.job;


import lombok.extern.slf4j.Slf4j;
import org.cxq.domain.strategy.model.vo.StrategyAwardStockKeyVO;
import org.cxq.domain.strategy.service.IRaffleStock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 更新奖品库存任务，不让更新库存压力达到数据库，用redis更新缓存数据库，异步队列更新数据库，数据库表最终一致
 */


@Slf4j
@Component()
public class UpdateAwardStockJob {
    @Resource
    private IRaffleStock iRaffleStock;

    @Scheduled(cron = "0/5 * * * * ?")
    public void exec() throws InterruptedException {
        try {
//            log.info("定时任务，更新奖品消耗库存【延迟队列获取，降低对数据库的更新频次，不要产生竞争】");
            StrategyAwardStockKeyVO strategyAwardStockKeyVO = iRaffleStock.takeQueueValue();
            if(strategyAwardStockKeyVO==null) return ;
            log.info("定时任务，更新奖品消耗库存 strategyId:{} awardId:{}",strategyAwardStockKeyVO.getStrategyId(),strategyAwardStockKeyVO.getAwardId());
            iRaffleStock.UpdateStrategyAwardStock(strategyAwardStockKeyVO.getStrategyId(), strategyAwardStockKeyVO.getAwardId());
          } catch (InterruptedException e) {
            log.error("定时任务，更新奖品消耗库存失败",e);
        }
    }
}
