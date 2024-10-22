package org.cxq.domain.strategy.service.rule.tree.factory.engine.impl;

import lombok.extern.slf4j.Slf4j;
import org.cxq.domain.strategy.model.vo.*;
import org.cxq.domain.strategy.service.rule.tree.ILogicTreeNode;
import org.cxq.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import org.cxq.domain.strategy.service.rule.tree.factory.engine.IDecisionTreeEngine;


import java.util.List;
import java.util.Map;


/**
 * 决策树引擎
 */
@Slf4j
public class DecisionTreeEngine implements IDecisionTreeEngine {
    private final Map<String, ILogicTreeNode> logicTreeNodeGroup;
    private final RuleTreeVO ruleTreeVO;

    public DecisionTreeEngine(Map<String, ILogicTreeNode> logicTreeNodeGroup, RuleTreeVO ruleTreeVO) {
        this.logicTreeNodeGroup = logicTreeNodeGroup;
        this.ruleTreeVO = ruleTreeVO;
    }


    /**
     *  首先创建一个为null的返回对象，然后处理基础信息：1、规则书根节点的类型 2、拿到存储所有节点的Map对象，
     *  先从Map中拿到根节点的节点对象，开始循环遍历：判断节点对象是否为空，如果不为空，就去logicTreeNodeGroup
     *  拿到它的ILogicTreeNode对象，调用logic方法，进行一些初始化工作，拿到当前节点类型的TreeActionEntity
     *  和RuleLogicCheckTypeVO对象，从TreeActionEntity获取到策略奖励实体，然后遍历当前节点的下一个节点。
     *
     * @param userId
     * @param strategyId
     * @param awardId
     * @return
     */
    @Override
    public DefaultTreeFactory.StrategyAwardVO process(String userId, Long strategyId, Integer awardId) {
        DefaultTreeFactory.StrategyAwardVO strategyAwardVO=null;

        //获取基础信息
        String nextNode = ruleTreeVO.getTreeRootRuleNode();
        Map<String, RuleTreeNodeVO> treeNodeMap = ruleTreeVO.getTreeNodeVOMap();

        RuleTreeNodeVO ruleTreeNode = treeNodeMap.get(nextNode);
        while(null!=nextNode){
            //获取决策节点
            ILogicTreeNode logicTreeNode = logicTreeNodeGroup.get(ruleTreeNode.getRuleKey());
            String ruleValue = ruleTreeNode.getRuleValue();
            //决策节点计算
            DefaultTreeFactory.TreeActionEntity logicEntity = logicTreeNode.logic(userId, strategyId, awardId,ruleValue);
            RuleLogicCheckTypeVO ruleLogicCheckTypeVO = logicEntity.getRuleLogicCheckType();
            strategyAwardVO=logicEntity.getStrategyAwardVO();
            log.info("决策树引擎【{}】treeId:{} node:{} code:{}", ruleTreeVO.getTreeName(), ruleTreeVO.getTreeId(), nextNode, ruleLogicCheckTypeVO.getCode());

            nextNode=nextNode(ruleLogicCheckTypeVO.getCode(),ruleTreeNode.getTreeNodeLineVOList());
            ruleTreeNode  = treeNodeMap.get(nextNode);
        }

        //返回最终结果
        return strategyAwardVO;
    }


    /**
     * 遍历规则树，将遍历到的当前节点与传进来的节点进行比较，如果比较结果为true，返回当前节点的下一个节点
     * @param matterValue
     * @param ruleTreeNodeLineVOList
     * @return
     */
    private String nextNode(String matterValue, List<RuleTreeNodeLineVO> ruleTreeNodeLineVOList){
        if(ruleTreeNodeLineVOList==null || ruleTreeNodeLineVOList.isEmpty()) return null;
        for (RuleTreeNodeLineVO nodeLine : ruleTreeNodeLineVOList) {
            if(decisionLogic(matterValue,nodeLine)){
                return nodeLine.getRuleNodeTo();
            }
        }
        return null;
    }

    /**
     * 如果传进来的值与当前节点的code值相同，返回true
     * @param matterValue
     * @param nodeLine
     * @return
     */
    public boolean decisionLogic(String matterValue, RuleTreeNodeLineVO nodeLine) {
        switch (nodeLine.getRuleLimitType()) {
            case EQUAL:
                return matterValue.equals(nodeLine.getRuleLimitValue().getCode());
            // 以下规则暂时不需要实现
            case GT:
            case LT:
            case GE:
            case LE:
            default:
                return false;
        }
    }
}
