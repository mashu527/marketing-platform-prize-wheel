package org.cxq.domain.strategy.service.rule.tree.impl;


import lombok.extern.slf4j.Slf4j;
import org.cxq.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import org.cxq.domain.strategy.model.vo.StrategyAwardStockKeyVO;
import org.cxq.domain.strategy.repository.IStrategyRepository;
import org.cxq.domain.strategy.service.armory.IStrategyDispatch;
import org.cxq.domain.strategy.service.rule.tree.ILogicTreeNode;
import org.cxq.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 库存扣减节点
 */

@Slf4j
@Component("rule_stock")
public class RuleStockLogicTreeNode implements ILogicTreeNode {
    @Resource
    private IStrategyRepository iStrategyRepository;
    @Resource
    private IStrategyDispatch iStrategyDispatch;

    @Override
    public DefaultTreeFactory.TreeActionEntity logic(String userId, Long strategyId, Integer awardId,String ruleValue) {
        log.info("规则过滤-库存扣减 userId:{} strategyId:{} awardId:{}",userId,strategyId,awardId);
        //扣减库存
        Boolean status = iStrategyDispatch.subtractionAwardStock(strategyId, awardId);
        //status true 扣减成功
        if(status){
            //写入延迟队列,延迟消息更新数据库记录
            iStrategyRepository.awardStockConsumeSendQueue(StrategyAwardStockKeyVO.builder()
                    .strategyId(strategyId)
                    .awardId(awardId)
                    .build());

            return DefaultTreeFactory.TreeActionEntity.builder()
                    .ruleLogicCheckType(RuleLogicCheckTypeVO.TAKE_OVER)
                    .strategyAwardVO(DefaultTreeFactory.StrategyAwardVO.builder()
                            .awardId(awardId)
                            .awardRuleValue("")
                            .build())
                    .build();
        }


        return DefaultTreeFactory.TreeActionEntity.builder()
                .ruleLogicCheckType(RuleLogicCheckTypeVO.ALLOW)
                .build();
    }
}
