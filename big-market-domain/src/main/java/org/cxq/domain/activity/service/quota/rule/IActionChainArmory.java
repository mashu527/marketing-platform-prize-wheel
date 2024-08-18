package org.cxq.domain.activity.service.quota.rule;


/**
 * 抽奖动作装配
 */
public interface IActionChainArmory {

    IActionChain next();

    IActionChain appendNext(IActionChain next);

}
