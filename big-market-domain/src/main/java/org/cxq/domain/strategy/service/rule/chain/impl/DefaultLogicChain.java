package org.cxq.domain.strategy.service.rule.chain.impl;

import lombok.extern.slf4j.Slf4j;
import org.cxq.domain.strategy.service.armory.IStrategyDispatch;
import org.cxq.domain.strategy.service.rule.AbstractLogicChain;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component("default")
public class DefaultLogicChain extends AbstractLogicChain {
    @Resource
    private IStrategyDispatch iStrategyDispatch;

    @Override
    public Integer logic(String userId, Long strategyId) {
        Integer awardId = iStrategyDispatch.getRandomAwardId(strategyId);
        log.info("抽奖责任链-默认处理 userId:{} strategyId:{} ruleModel:{} awardId:{}",userId,strategyId,ruleModel(),awardId);

        return awardId;
    }


    @Override
    protected String ruleModel() {
        return "default";
    }
}
