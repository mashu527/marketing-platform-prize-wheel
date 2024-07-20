package org.cxq.domain.strategy.service.rule.tree.impl;


import lombok.extern.slf4j.Slf4j;
import org.cxq.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import org.cxq.domain.strategy.service.rule.tree.ILogicTreeNode;
import org.cxq.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import org.springframework.stereotype.Component;

/**
 * 次数锁节点
 */

@Slf4j
@Component("rule_lock")
public class RuleLockLogicTreeNode implements ILogicTreeNode {
    @Override
    public DefaultTreeFactory.TreeActionEntity logic(String userId, Long strategyId, Integer awardId) {
        

        return DefaultTreeFactory.TreeActionEntity.builder()
                .ruleLogicCheckType(RuleLogicCheckTypeVO.ALLOW)
                .build();
    }
}
