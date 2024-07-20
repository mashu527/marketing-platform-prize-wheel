package org.cxq.domain.strategy.service.rule.chain.factory;

import lombok.*;
import org.cxq.domain.strategy.model.entity.StrategyEntity;
import org.cxq.domain.strategy.repository.IStrategyRepository;
import org.cxq.domain.strategy.service.rule.chain.ILogicChain;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
public class DefaultChainFactory {
    private final Map<String, ILogicChain> logicChainGroup;

    private final IStrategyRepository iStrategyRepository;

    public DefaultChainFactory(Map<String,ILogicChain> logicChainGroup,IStrategyRepository iStrategyRepository){
        this.logicChainGroup=logicChainGroup;
        this.iStrategyRepository=iStrategyRepository;
    }

    public ILogicChain openLogicChain(Long strategyId){
        StrategyEntity strategy = iStrategyRepository.queryStrategyByStrategyId(strategyId);
        String[] ruleModels = strategy.ruleModels();

        // 如果未配置策略规则，则只装填一个默认责任链
        if (null == ruleModels || 0 == ruleModels.length) return logicChainGroup.get("default");

        // 按照配置顺序装填用户配置的责任链；rule_blacklist、rule_weight 「注意此数据从Redis缓存中获取，如果更新库表，记得在测试阶段手动处理缓存」
        ILogicChain logicChain = logicChainGroup.get(ruleModels[0]);
        ILogicChain current = logicChain;
        for (int i = 1; i < ruleModels.length; i++) {
            ILogicChain nextChain = logicChainGroup.get(ruleModels[i]);
            current = current.appendNext(nextChain);
        }

        // 责任链的最后装填默认责任链
        current.appendNext(logicChainGroup.get("default"));

        return logicChain;
    }



    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class StrategyAwardVO {
        /**
         * 奖品id
         */
        private Integer awardId;
        /**
         * 模型信息
         */
        private String logicModel;
    }


    @AllArgsConstructor
    @Getter
    public enum LogicModel {
        RULE_DEFAULT("rule_default","默认抽奖"),
        RULE_BACKLIST("rule_backlist","黑名单抽奖"),
        RULE_WEIGHT("rule_weight","权重规则"),
        ;

        private final String code;
        private final String info;
    }

}
