package org.cxq.domain.strategy.service.rule.tree;


import org.cxq.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;

/**
 * 规则树接口
 */

public interface ILogicTreeNode {
    DefaultTreeFactory.TreeActionEntity logic(String userId, Long strategy, Integer awardId,String ruleValue);
}
