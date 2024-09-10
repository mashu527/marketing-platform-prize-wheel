package org.cxq.domain.strategy.service.rule.chain;

/**
 * 装配接口
 */
public interface  ILogicChainArmory {

    ILogicChain appendNext(ILogicChain next);

    ILogicChain next();
}
