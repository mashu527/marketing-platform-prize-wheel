package org.cxq.domain.strategy.service.rule.chain.impl;

import lombok.extern.slf4j.Slf4j;
import org.cxq.domain.strategy.service.armory.IStrategyDispatch;
import org.cxq.domain.strategy.service.rule.AbstractLogicChain;
import org.cxq.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component("default")
public class DefaultLogicChain extends AbstractLogicChain {
    @Resource
    private IStrategyDispatch iStrategyDispatch;

    @Override
    public DefaultChainFactory.StrategyAwardVO logic(String userId, Long strategyId) {
        Integer awardId = iStrategyDispatch.getRandomAwardId(strategyId);
        log.info("抽奖责任链-默认处理 userId:{} strategyId:{} ruleModel:{} awardId:{}",userId,strategyId,ruleModel(),awardId);

        return DefaultChainFactory.StrategyAwardVO.builder()
                .awardId(awardId)
                .logicModel(ruleModel())
                .build();
    }


    @Override
    protected String ruleModel() {
        return DefaultChainFactory.LogicModel.RULE_DEFAULT.getCode();
    }
}
