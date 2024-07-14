package org.cxq.domain.strategy.service.raffle;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.cxq.domain.strategy.model.entity.RaffleAwardEntity;
import org.cxq.domain.strategy.model.entity.RaffleFactorEntity;
import org.cxq.domain.strategy.model.entity.RuleActionEntity;
import org.cxq.domain.strategy.model.entity.StrategyEntity;
import org.cxq.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import org.cxq.domain.strategy.model.vo.StrategyAwardRuleModelVO;
import org.cxq.domain.strategy.repository.IStrategyRepository;
import org.cxq.domain.strategy.service.IRaffleStrategy;
import org.cxq.domain.strategy.service.armory.IStrategyDispatch;
import org.cxq.domain.strategy.service.rule.factory.DefaultLogicFactory;
import org.cxq.types.enums.ResponseCode;
import org.cxq.types.exception.AppException;

@Slf4j
@AllArgsConstructor
public abstract class AbstractRaffleStrategy implements IRaffleStrategy {

    protected IStrategyRepository iStrategyRepository;
    protected IStrategyDispatch iStrategyDispatch;

//    public AbstractRaffleStrategy(IStrategyRepository iStrategyRepository,IStrategyDispatch iStrategyDispatch){
//        this.iStrategyRepository=iStrategyRepository;
//        this.iStrategyDispatch=iStrategyDispatch;
//    }
    @Override
    public RaffleAwardEntity performRaffle(RaffleFactorEntity raffleFactorEntity) {
        // 1. 参数校验
        String userId = raffleFactorEntity.getUserId();
        Long strategyId = raffleFactorEntity.getStrategyId();
        if (null == strategyId || StringUtils.isBlank(userId)) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
        }

        //查询后抽奖策略
        StrategyEntity strategy=iStrategyRepository.queryStrategyByStrategyId(strategyId);

        //抽奖前的规则过滤
        RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> ruleActionEntity=this.doCheckRaffleBeforeLogic(RaffleFactorEntity.builder().userId(userId).strategyId(strategyId).build(),strategy.ruleModels());

        if(ruleActionEntity.getCode().equals(RuleLogicCheckTypeVO.TAKE_OVER.getCode())){
            if(DefaultLogicFactory.LogicModel.RULE_BLACKLIST.getCode().equals(ruleActionEntity.getRuleModel())){
                //规则为黑名但则返回固定的奖品
                return RaffleAwardEntity.builder()
                                        .awardId(ruleActionEntity.getData().getAwardId())
                                        .build();
            } else if (DefaultLogicFactory.LogicModel.RULE_WIGHT.getCode().equals(ruleActionEntity.getRuleModel())) {
                String ruleWeightValueKey = ruleActionEntity.getData().getRuleWeightValueKey();

                Integer awardId = iStrategyDispatch.getRandomAwardId(strategyId, ruleWeightValueKey);

                return RaffleAwardEntity.builder()
                        .awardId(awardId)
                        .build();
            }
        }

        //抽取随机奖品
        Integer awardId = iStrategyDispatch.getRandomAwardId(strategyId);

        //查询奖品规则 「抽奖中（拿到奖品ID时，过滤规则）、抽奖后（扣减完奖品库存后过滤，抽奖中拦截和无库存则走兜底）」
        StrategyAwardRuleModelVO strategyAwardRuleModelVO=iStrategyRepository.queryStrategyAwardRuleModel(strategyId,awardId);

        //抽奖规则过滤
        RuleActionEntity<RuleActionEntity.RaffleCenterEntity> ruleActionCenterEntity=this.doCheckRaffleCenterLogic
                (RaffleFactorEntity.builder().strategyId(strategyId).userId(userId).awardId(awardId).build(),
                        strategyAwardRuleModelVO.raffleCenterRuleModelList());

        if (RuleLogicCheckTypeVO.TAKE_OVER.getCode().equals(ruleActionCenterEntity.getCode())){
            log.info("【临时日志】中奖中规则拦截，通过抽奖后规则 rule_luck_award 走兜底奖励。");
            return RaffleAwardEntity.builder()
                    .awardDesc("中奖中规则拦截，通过抽奖后规则 rule_luck_award 走兜底奖励。")
                    .build();
        }

        return RaffleAwardEntity.builder()
                .awardId(awardId)
                .build();

    }

    protected abstract RuleActionEntity<RuleActionEntity.RaffleCenterEntity> doCheckRaffleCenterLogic(RaffleFactorEntity build, String ...logics);

    protected abstract RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> doCheckRaffleBeforeLogic(RaffleFactorEntity build, String ...logics);


}
