package org.cxq.domain.strategy.service.rule.chain.impl;

import lombok.extern.slf4j.Slf4j;
import org.cxq.domain.strategy.repository.IStrategyRepository;
import org.cxq.domain.strategy.service.rule.AbstractLogicChain;
import org.cxq.types.common.Constants;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


/**
 * 黑名单方法
 */
@Slf4j
@Component("rule_backlist")
public class BackListLogicChain extends AbstractLogicChain {
    @Resource
    private IStrategyRepository iStrategyRepository;

    @Override
    public Integer logic(String userId, Long strategyId) {
        log.info("抽奖责任链-黑名单开始 userId:{} strategyId:{} ruleModel:{}",userId,strategyId,ruleModel());
        String ruleValue=iStrategyRepository.queryStrategyRuleValue(strategyId,ruleModel());

        String[] splitRuleValue = ruleValue.split(Constants.COLON);
        Integer awardId = Integer.parseInt(splitRuleValue[0]);

        // 过滤其他规则
        String[] userBlackIds = splitRuleValue[1].split(Constants.SPLIT);
        for (String userBlackId : userBlackIds) {
            if(userId.equals(userBlackId)){
                log.info("抽奖责任链-黑名单接管 userId:{} strategyId:{} ruleModel:{}",userId,strategyId,ruleModel());
                return awardId;
            }
        }

        log.info("抽奖责任链-黑名单放行 userId:{} strategyId:{} ruleModel:{}",userId,strategyId,ruleModel());
        return next().logic(userId,strategyId);
    }

    @Override
    protected String ruleModel() {
        return "rule_backlist";
    }
}
