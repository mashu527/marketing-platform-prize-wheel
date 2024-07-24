package org.cxq.domain.strategy.service;


import org.cxq.domain.strategy.model.vo.StrategyAwardStockKeyVO;

/**
 * 抽奖库存相关服务，获取库存消息队列
 */
public interface IRaffleStock {
    StrategyAwardStockKeyVO takeQueueValue() throws InterruptedException;

    void UpdateStrategyAwardStock(Long strategyId,Integer awardId);

}
