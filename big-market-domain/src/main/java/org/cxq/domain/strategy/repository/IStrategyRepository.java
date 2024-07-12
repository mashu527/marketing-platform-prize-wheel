package org.cxq.domain.strategy.repository;


import org.cxq.domain.strategy.model.entity.StrategyAwardEntity;
import org.cxq.domain.strategy.model.entity.StrategyEntity;
import org.cxq.domain.strategy.model.entity.StrategyRuleEntity;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

/**
 * 仓储服务接口
 */
public interface IStrategyRepository {
    List<StrategyAwardEntity> queryStrategyAwardList(Long strategyId);

    void storeStrategyAwardSearchRateTables(String key, Integer rateRange, HashMap<Integer, Integer> shuffleStrategyAwardSearchRateTables);

    int getRateRange(Long strategyId);

    int getRateRange(String key);

    Integer getStrategyAwardAssemble(String strategyId, int rateKey);

    StrategyEntity queryStrategyByStrategyId(Long strategyId);

    StrategyRuleEntity queryStrategyRule(Long StrategyId,String ruleWeight);

    String queryStrategyRuleValue(Long strategyId, Integer awardId, String ruleModel);


}