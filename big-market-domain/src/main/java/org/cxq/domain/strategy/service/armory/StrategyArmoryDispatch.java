package org.cxq.domain.strategy.service.armory;


import lombok.extern.slf4j.Slf4j;
import org.cxq.domain.strategy.model.entity.StrategyAwardEntity;
import org.cxq.domain.strategy.model.entity.StrategyEntity;
import org.cxq.domain.strategy.model.entity.StrategyRuleEntity;
import org.cxq.domain.strategy.repository.IStrategyRepository;
import org.cxq.types.common.Constants;
import org.cxq.types.enums.ResponseCode;
import org.cxq.types.exception.AppException;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.*;

@Slf4j
@Service
public class StrategyArmoryDispatch implements IStrategyArmory,IStrategyDispatch{
    @Resource
    private IStrategyRepository iStrategyRepository;

    @Override
    public boolean assembleLotteryStrategy(Long strategyId) {
        //查询策略配置
        List<StrategyAwardEntity> strategyAwardEntities=iStrategyRepository.queryStrategyAwardList(strategyId);

        for (StrategyAwardEntity strategyAwardEntity : strategyAwardEntities) {
            Integer awardId = strategyAwardEntity.getAwardId();
            Integer awardCount = strategyAwardEntity.getAwardCount();
            cacheStrategyAwardCount(strategyId,awardId,awardCount);
        }
        
        //调用方法进行装配
        assembleLotteryStrategy(String.valueOf(strategyId),strategyAwardEntities);

        //权重规则配置
        StrategyEntity strategyEntity=iStrategyRepository.queryStrategyByStrategyId(strategyId);
        String ruleWeight = strategyEntity.getRuleWeight();
        if(ruleWeight==null) return true;

        StrategyRuleEntity strategyRuleEntity=iStrategyRepository.queryStrategyRule(strategyId,ruleWeight);
        if(strategyRuleEntity==null){
            throw new AppException(ResponseCode.STRATEGY_RULE_WEIGHT_IS_NULL.getCode(),ResponseCode.STRATEGY_RULE_WEIGHT_IS_NULL.getInfo());
        }


        //装配包含规则为rule_weight的概率表
        Map<String, List<Integer>> ruleWeightVauleMap = strategyRuleEntity.getRuleWeightVaules();
        Set<String> keys = ruleWeightVauleMap.keySet();
        for (String key : keys) {
            List<Integer> ruleWeightVaules = ruleWeightVauleMap.get(key);
            ArrayList<StrategyAwardEntity> strategyAwardEntitiesClone = new ArrayList<>(strategyAwardEntities);
            strategyAwardEntitiesClone.removeIf(strategyAwardEntity ->!ruleWeightVaules.contains(strategyAwardEntity.getAwardId()));
            assembleLotteryStrategy(String.valueOf(strategyId).concat("_").concat(key),strategyAwardEntitiesClone);
        }

        return true;
    }

    @Override
    public boolean assembleLotteryStrategyByActivityId(Long activityId) {
        Long strategyId=iStrategyRepository.queryStrategyIdByActivityId(activityId);
        return assembleLotteryStrategy(strategyId);
    }

    private void cacheStrategyAwardCount(Long strategyId,Integer awardId, Integer awardCount) {
        String cacheKey= Constants.RedisKey.STRATEGY_AWARD_COUNT_KEY+strategyId+Constants.UNDERLINE+awardId;
        iStrategyRepository.cacheStrategyAwardCount(cacheKey,awardCount);
    }


    /**
     * 代码实现的功能：我们传入两参数，分别为策略id，策略奖品表，然后求出表中奖品的最大概率之和最小概率值，
     *              根据最小概率值创建一张概率分布表，在为不同概率分布不同的空间长度，将奖品id填入其中，
     *              然后将概率分布表数据打乱，最后将这张表放入HashMap中
     * @param key
     * @param strategyAwardEntities
     */
    public void assembleLotteryStrategy(String key,List<StrategyAwardEntity> strategyAwardEntities){
        //获取最小概率值
        BigDecimal minAwardRate = strategyAwardEntities.stream()
                .map(StrategyAwardEntity::getAwardRate)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        //获取概率总和
        BigDecimal totalAwardRate = strategyAwardEntities.stream()
                .map(StrategyAwardEntity::getAwardRate)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        //获取概率范围
        BigDecimal rateRange = totalAwardRate.divide(minAwardRate, 0, RoundingMode.CEILING);

        ArrayList<Integer> strategyAwardSearchRateTables = new ArrayList<>(rateRange.intValue());
        for (StrategyAwardEntity strategyAward : strategyAwardEntities) {
            Integer awardId = strategyAward.getAwardId();
            BigDecimal awardRate = strategyAward.getAwardRate();

            //计算每个概率值需要存放到查找表的位置,用id田中表格
            for(int i=0;i<rateRange.multiply(awardRate).setScale(0,RoundingMode.CEILING).intValue();i++){
                strategyAwardSearchRateTables.add(awardId);
            }
        }

        //乱序
        Collections.shuffle(strategyAwardSearchRateTables);


        HashMap<Integer, Integer> shuffleStrategyAwardSearchRateTables = new HashMap<>();
        for(int i=0;i<strategyAwardSearchRateTables.size();i++){
            shuffleStrategyAwardSearchRateTables.put(i,strategyAwardSearchRateTables.get(i));
        }

        //存储到Redis
        iStrategyRepository.storeStrategyAwardSearchRateTables(key,shuffleStrategyAwardSearchRateTables.size(),shuffleStrategyAwardSearchRateTables);
    }

    @Override
    public Integer getRandomAwardId(Long strategyId) {
        int rateRange=iStrategyRepository.getRateRange(strategyId);
        return iStrategyRepository.getStrategyAwardAssemble(String.valueOf(strategyId),new SecureRandom().nextInt(rateRange));
    }

    @Override
    public Integer getRandomAwardId(Long strategyId, String ruleWeightValue) {
        String key=String.valueOf(strategyId).concat("_").concat(ruleWeightValue);
        int rateRange=iStrategyRepository.getRateRange(key);
        return iStrategyRepository.getStrategyAwardAssemble(key,new SecureRandom().nextInt(rateRange));
    }

    @Override
    public Boolean subtractionAwardStock(Long strategyId, Integer awardId) {
        String cacheKey= Constants.RedisKey.STRATEGY_AWARD_COUNT_KEY+strategyId+Constants.UNDERLINE+awardId;
        return iStrategyRepository.subtractionAwardStock(cacheKey);
    }
}
