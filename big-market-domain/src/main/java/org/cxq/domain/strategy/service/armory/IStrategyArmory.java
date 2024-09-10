package org.cxq.domain.strategy.service.armory;


/**
 * 策略装配工厂（兵工厂），负责初始化策略计算
 */
public interface IStrategyArmory {

    boolean assembleLotteryStrategy(Long strategyId);

    boolean assembleLotteryStrategyByActivityId(Long activityId);
}
