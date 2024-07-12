package org.cxq.infrastructure.persistent.dao;


import org.apache.ibatis.annotations.Mapper;
import org.cxq.domain.strategy.model.entity.StrategyEntity;
import org.cxq.infrastructure.persistent.po.Strategy;
import org.cxq.infrastructure.persistent.po.StrategyRule;

import java.util.List;

@Mapper
public interface IStrategyDao {

    List<Strategy>queryStrategyList();

    Strategy queryStrategyByStrategyId(Long strategyId);

    String queryStrategyRuleValue(StrategyRule strategyRule);

}
