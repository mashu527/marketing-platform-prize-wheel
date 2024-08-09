package org.cxq.domain.strategy.service.rule.chain;


import org.cxq.domain.strategy.service.rule.chain.factory.DefaultChainFactory;

/**
 * 责任链接口
 */
public interface ILogicChain extends ILogicChainArmory{

    /**
     * 责任链接口
     * @param userId
     * @param strategyId
     * @return
     */
    DefaultChainFactory.StrategyAwardVO logic(String userId, Long strategyId);

}
