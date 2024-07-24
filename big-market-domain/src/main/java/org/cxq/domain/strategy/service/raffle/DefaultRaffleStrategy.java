package org.cxq.domain.strategy.service.raffle;


import lombok.extern.slf4j.Slf4j;
import org.cxq.domain.strategy.model.vo.RuleTreeVO;
import org.cxq.domain.strategy.model.vo.StrategyAwardRuleModelVO;
import org.cxq.domain.strategy.model.vo.StrategyAwardStockKeyVO;
import org.cxq.domain.strategy.repository.IStrategyRepository;
import org.cxq.domain.strategy.service.AbstractRaffleStrategy;
import org.cxq.domain.strategy.service.armory.IStrategyDispatch;
import org.cxq.domain.strategy.service.rule.chain.ILogicChain;
import org.cxq.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import org.cxq.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import org.cxq.domain.strategy.service.rule.tree.factory.engine.IDecisionTreeEngine;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DefaultRaffleStrategy extends AbstractRaffleStrategy {

    public DefaultRaffleStrategy(IStrategyRepository iStrategyRepository, IStrategyDispatch iStrategyDispatch, DefaultChainFactory defaultChainFactory, DefaultTreeFactory defaultTreeFactory) {
        super(iStrategyRepository, iStrategyDispatch, defaultChainFactory, defaultTreeFactory);
    }


    @Override
    public DefaultChainFactory.StrategyAwardVO raffleLogicChain(String userId, Long strategyId) {
        ILogicChain iLogicChain = defaultChainFactory.openLogicChain(strategyId);
        return iLogicChain.logic(userId,strategyId);
    }


    /**
     * 1.先根据奖品id和策略id去查询该奖品的规则模型，若没有配置相应的规则模型，直接返回抽奖结果
     * 2.调用queryRuleTreeVOByTreeId方法根据规则模型获取RuleTreeVO对象
     * 3.初始化规则树，对对应变量赋值，再调用process方法执行决策
     * @param userId
     * @param strategyId
     * @param awardId
     * @return
     */
    @Override
    public DefaultTreeFactory.StrategyAwardVO raffleLogicTree(String userId, Long strategyId, Integer awardId) {
        StrategyAwardRuleModelVO strategyAwardRuleModelVO = iStrategyRepository.queryStrategyAwardRuleModel(strategyId, awardId);
        if(strategyAwardRuleModelVO==null){
            return DefaultTreeFactory.StrategyAwardVO.builder()
                    .awardId(awardId)
                    .build();
        }
        RuleTreeVO ruleTreeVO = iStrategyRepository.queryRuleTreeVOByTreeId(strategyAwardRuleModelVO.getRuleModels());
        if(ruleTreeVO==null){
            throw new RuntimeException("存在抽奖策略配置的规则模型 Key，未在库表 rule_tree、rule_tree_node、rule_tree_line 配置对应的规则树信息 " + strategyAwardRuleModelVO.getRuleModels());
        }

        IDecisionTreeEngine treeEngine = defaultTreeFactory.openLogicTree(ruleTreeVO);
        return treeEngine.process(userId, strategyId, awardId);
    }

    @Override
    public StrategyAwardStockKeyVO takeQueueValue() throws InterruptedException {
        return iStrategyRepository.takeQueueValuee();
    }

    @Override
    public void UpdateStrategyAwardStock(Long strategyId, Integer awardId) {
        iStrategyRepository.updateStrategyAwardStock(strategyId,awardId);
    }




//    @Override
//    protected RuleActionEntity<RuleActionEntity.RaffleCenterEntity> doCheckRaffleCenterLogic(RaffleFactorEntity raffleFactorEntity, String... logics) {
//        if(logics==null || logics.length==0){
//            return RuleActionEntity.<RuleActionEntity.RaffleCenterEntity>builder()
//                    .code(RuleLogicCheckTypeVO.ALLOW.getCode())
//                    .info(RuleLogicCheckTypeVO.ALLOW.getInfo())
//                    .build();
//        }
//
//        Map<String, ILogicFilter<RuleActionEntity.RaffleCenterEntity>> logicFilterGroup = defaultLogicFactory.openLogicFilter();
//
//        RuleActionEntity<RuleActionEntity.RaffleCenterEntity> ruleActionEntity=null;
//        for (String ruleModel : logics) {
//            ILogicFilter<RuleActionEntity.RaffleCenterEntity> logicFilter = logicFilterGroup.get(ruleModel);
//            RuleMatterEntity ruleMatterEntity = new RuleMatterEntity();
//            ruleMatterEntity.setUserId(raffleFactorEntity.getUserId());
//            ruleMatterEntity.setAwardId(raffleFactorEntity.getAwardId());
//            ruleMatterEntity.setStrategyId(raffleFactorEntity.getStrategyId());
//            ruleMatterEntity.setRuleModel(ruleModel);
//            ruleActionEntity = logicFilter.filter(ruleMatterEntity);
//            // 非放行结果则顺序过滤
//            log.info("抽奖中规则过滤 userId: {} ruleModel: {} code: {} info: {}", raffleFactorEntity.getUserId(), ruleModel, ruleActionEntity.getCode(), ruleActionEntity.getInfo());
//            if (!RuleLogicCheckTypeVO.ALLOW.getCode().equals(ruleActionEntity.getCode())) return ruleActionEntity;
//        }
//
//        return ruleActionEntity;
//
//    }
//
//    @Override
//    protected RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> doCheckRaffleBeforeLogic(RaffleFactorEntity raffleFactorEntity, String... logics) {
//        if (logics == null || 0 == logics.length) return RuleActionEntity.<RuleActionEntity.RaffleBeforeEntity>builder()
//                .code(RuleLogicCheckTypeVO.ALLOW.getCode())
//                .info(RuleLogicCheckTypeVO.ALLOW.getInfo())
//                .build();
//
//        Map<String, ILogicFilter<RuleActionEntity.RaffleBeforeEntity>> iLogicFilterGroup = defaultLogicFactory.openLogicFilter();
//
//        //黑名单规则优先过滤
//        String ruleBackList = Arrays.stream(logics)
//                .filter(str -> str.contains(DefaultLogicFactory.LogicModel.RULE_BLACKLIST.getCode()))
//                .findFirst()
//                .orElse(null);
//
//
//        if (StringUtils.isNotBlank(ruleBackList)) {
//            ILogicFilter<RuleActionEntity.RaffleBeforeEntity> logicFilter = iLogicFilterGroup.get(DefaultLogicFactory.LogicModel.RULE_BLACKLIST.getCode());
//            RuleMatterEntity ruleMatterEntity = new RuleMatterEntity();
//            ruleMatterEntity.setUserId(raffleFactorEntity.getUserId());
//            ruleMatterEntity.setAwardId(ruleMatterEntity.getAwardId());
//            ruleMatterEntity.setStrategyId(raffleFactorEntity.getStrategyId());
//            ruleMatterEntity.setRuleModel(DefaultLogicFactory.LogicModel.RULE_BLACKLIST.getCode());
//            RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> ruleActionEntity = logicFilter.filter(ruleMatterEntity);
//            if (!RuleLogicCheckTypeVO.ALLOW.getCode().equals(ruleActionEntity.getCode())) {
//                return ruleActionEntity;
//            }
//        }
//
//
//        //顺序过滤剩余规则
//        List<String> ruleList = Arrays.stream(logics)
//                .filter(s -> !s.equals(DefaultLogicFactory.LogicModel.RULE_BLACKLIST.getCode()))
//                .collect(Collectors.toList());
//
//        RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> ruleActionEntity = null;
//        for (String ruleModel : ruleList) {
//            ILogicFilter<RuleActionEntity.RaffleBeforeEntity> logicFilter = iLogicFilterGroup.get(ruleModel);
//            RuleMatterEntity ruleMatterEntity = new RuleMatterEntity();
//            ruleMatterEntity.setUserId(raffleFactorEntity.getUserId());
//            ruleMatterEntity.setAwardId(ruleMatterEntity.getAwardId());
//            ruleMatterEntity.setStrategyId(raffleFactorEntity.getStrategyId());
//            ruleMatterEntity.setRuleModel(ruleModel);
//            ruleActionEntity = logicFilter.filter(ruleMatterEntity);
//            // 非放行结果则顺序过滤
//            log.info("抽奖前规则过滤 userId: {} ruleModel: {} code: {} info: {}", raffleFactorEntity.getUserId(), ruleModel, ruleActionEntity.getCode(), ruleActionEntity.getInfo());
//            if (!RuleLogicCheckTypeVO.ALLOW.getCode().equals(ruleActionEntity.getCode())) return ruleActionEntity;
//        }
//
//        return ruleActionEntity;
//    }



}
