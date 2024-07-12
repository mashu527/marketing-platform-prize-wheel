package org.cxq.infrastructure.persistent.dao;


import org.apache.ibatis.annotations.Mapper;
import org.cxq.infrastructure.persistent.po.StrategyRule;

import java.util.List;

@Mapper
public interface IStrategyRuleDao {

    List<StrategyRule>queryStrategyRuleList();

    StrategyRule queryStrategyRule(StrategyRule strategyRule);
}
