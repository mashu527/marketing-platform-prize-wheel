package org.cxq.domain.strategy.service;


import org.cxq.domain.strategy.model.entity.RaffleAwardEntity;
import org.cxq.domain.strategy.model.entity.RaffleFactorEntity;

/**
 * 抽奖策略接口
 */
public interface IRaffleStrategy {
    RaffleAwardEntity performRaffle(RaffleFactorEntity raffleFactorEntity);
}
