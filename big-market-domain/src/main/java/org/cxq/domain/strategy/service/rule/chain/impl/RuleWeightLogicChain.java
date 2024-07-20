package org.cxq.domain.strategy.service.rule.chain.impl;

import lombok.extern.slf4j.Slf4j;
import org.cxq.domain.strategy.repository.IStrategyRepository;
import org.cxq.domain.strategy.service.armory.IStrategyDispatch;
import org.cxq.domain.strategy.service.rule.AbstractLogicChain;
import org.cxq.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import org.cxq.types.common.Constants;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@Component("rule_weight")
public class RuleWeightLogicChain extends AbstractLogicChain {

    @Resource
    private IStrategyRepository iStrategyRepository;
    @Resource
    private IStrategyDispatch iStrategyDispatch;
    public Long userScore=0L;

    @Override
    public DefaultChainFactory.StrategyAwardVO logic(String userId, Long strategyId) {
        log.info("规则过滤-权重范围 userId:{} strategyId:{} ruleModel:{}",userId,strategyId,ruleModel());

        String ruleValue = iStrategyRepository.queryStrategyRuleValue(strategyId, ruleModel());

        //获取
        Map<Long, String> analyticalValueGroup = getAnalyticalValue(ruleValue);
        if (null == analyticalValueGroup || analyticalValueGroup.isEmpty()) {
            return null;
        }


        //将keys值排序，即4000,5000,6000
        ArrayList<Long> analyticalSortedKeys = new ArrayList<>(analyticalValueGroup.keySet());
        Collections.sort(analyticalSortedKeys);

        // 3. 找出最小符合的值，也就是【4500 积分，能找到 4000:102,103,104,105】、【5000 积分，能找到 5000:102,103,104,105,106,107】
        Long nextValue = analyticalSortedKeys.stream()
                .sorted(Comparator.reverseOrder())
                .filter(analyticalSortedKeyValue -> userScore >= analyticalSortedKeyValue)
                .findFirst()
                .orElse(null);

        if(nextValue!=null){
            Integer awardId=iStrategyDispatch.getRandomAwardId(strategyId,analyticalValueGroup.get(nextValue));
            log.info("规则过滤-权重范围 userId:{} strategyId:{} ruleModel:{} awardId:{}",userId,strategyId,ruleModel(),awardId);
            return DefaultChainFactory.StrategyAwardVO.builder()
                    .awardId(awardId)
                    .logicModel(ruleModel())
                    .build();
        }

        //过滤其他责任链
        log.info("规则过滤-权重范围 userId:{} strategyId:{} ruleModel:{}",userId,strategyId,ruleModel());
        return next().logic(userId,strategyId);
    }

    @Override
    protected String ruleModel() {
        return DefaultChainFactory.LogicModel.RULE_WEIGHT.getCode();
    }

    private Map<Long, String> getAnalyticalValue(String ruleValue) {
        String[] ruleValueGroups = ruleValue.split(Constants.SPACE);
        Map<Long, String> ruleValueMap = new HashMap<>();
        for (String ruleValueKey : ruleValueGroups) {
            // 检查输入是否为空
            if (ruleValueKey == null || ruleValueKey.isEmpty()) {
                return ruleValueMap;
            }
            // 分割字符串以获取键和值
            String[] parts = ruleValueKey.split(Constants.COLON);
            if (parts.length != 2) {
                throw new IllegalArgumentException("rule_weight rule_rule invalid input format" + ruleValueKey);
            }
            ruleValueMap.put(Long.parseLong(parts[0]), ruleValueKey);
        }
        return ruleValueMap;
    }
    public Map<String, List<Integer>> getRuleWeightVaule(String ruleValue){
        String[] ruleValueGroups = ruleValue.split(Constants.SPACE);
        HashMap<String, List<Integer>> resultMap = new HashMap<>();

        for (String ruleValueGroup : ruleValueGroups) {
            if(ruleValueGroup==null||ruleValueGroup.isEmpty()){
                return resultMap;
            }

            String[] parts = ruleValueGroup.split(Constants.COLON);
            if(parts.length!=2){
                throw new IllegalArgumentException("rule_weight rule_rule invalid input format"+ruleValueGroup);
            }

            String[] valueStrings = parts[1].split(Constants.SPLIT);
            ArrayList<Integer> values = new ArrayList<>();
            for (String valueString : valueStrings) {
                values.add(Integer.parseInt(valueString));
            }

            resultMap.put(ruleValueGroup,values);
        }

        return resultMap;
    }
}
