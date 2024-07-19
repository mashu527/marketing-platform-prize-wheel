package org.cxq.infrastructure.persistent.repository;



import org.cxq.domain.strategy.model.entity.StrategyAwardEntity;
import org.cxq.domain.strategy.model.entity.StrategyEntity;
import org.cxq.domain.strategy.model.entity.StrategyRuleEntity;
import org.cxq.domain.strategy.model.vo.StrategyAwardRuleModelVO;
import org.cxq.domain.strategy.repository.IStrategyRepository;
import org.cxq.infrastructure.persistent.dao.IStrategyAwardDao;
import org.cxq.infrastructure.persistent.dao.IStrategyDao;
import org.cxq.infrastructure.persistent.dao.IStrategyRuleDao;
import org.cxq.infrastructure.persistent.po.Strategy;
import org.cxq.infrastructure.persistent.po.StrategyAward;
import org.cxq.infrastructure.persistent.po.StrategyRule;
import org.cxq.infrastructure.persistent.redis.IRedisService;
import org.cxq.types.common.Constants;
import org.redisson.api.RMap;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 策略仓储实现
 */
@Repository
public class StrategyRepository implements IStrategyRepository {
    @Resource
    private IRedisService iRedisService;
    @Resource
    private IStrategyAwardDao iStrategyAwardDao;
    @Resource
    private IStrategyDao iStrategyDao;
    @Resource
    private IStrategyRuleDao iStrategyRuleDao;

    @Override
    public List<StrategyAwardEntity> queryStrategyAwardList(Long strategyId) {
        String cacheKey=Constants.RedisKey.STRATEGY_AWARD_KEY+ strategyId;

        ArrayList<StrategyAwardEntity> strategyAwardEntities = iRedisService.getValue(cacheKey);

        if(strategyAwardEntities!=null&&strategyAwardEntities.size()>0) return strategyAwardEntities;

        List<StrategyAward> strategyAwards = iStrategyAwardDao.queryStrategyAwardListByStrategyId(strategyId);
        strategyAwardEntities = new ArrayList<>(strategyAwards.size());
        for (StrategyAward strategyAward : strategyAwards) {
            StrategyAwardEntity strategyAwardEntity=StrategyAwardEntity.builder()
                    .strategyId(strategyAward.getStrategyId())
                    .awardId(strategyAward.getAwardId())
                    .awardCount(strategyAward.getAwardCount())
                    .awardCountSurplus(strategyAward.getAwardCountSurplus())
                    .awardRate(strategyAward.getAwardRate())
                    .build();
            strategyAwardEntities.add(strategyAwardEntity);
        }

        return strategyAwardEntities;
    }

    @Override
    public void storeStrategyAwardSearchRateTables(String key, Integer rateRange, HashMap<Integer, Integer> shuffleStrategyAwardSearchRateTables) {
        iRedisService.setValue(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY+key,rateRange.intValue());

        Map<Object, Object> cacheRateTable = iRedisService.getMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + key);
        cacheRateTable.putAll(shuffleStrategyAwardSearchRateTables);
    }

    @Override
    public int getRateRange(Long strategyId) {
        return iRedisService.getValue(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY+strategyId);
    }

    @Override
    public int getRateRange(String key) {
        return iRedisService.getValue(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY+key);
    }

    @Override
    public Integer getStrategyAwardAssemble(String strategyId, int rateKey) {
        return iRedisService.getFromMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY+strategyId,rateKey);
    }

    @Override
    public StrategyEntity queryStrategyByStrategyId(Long strategyId) {
        String cacheKey=Constants.RedisKey.STRATEGY_KEY + strategyId;
        StrategyEntity strategyEntity = iRedisService.getValue(cacheKey);
        if(strategyEntity!=null) return strategyEntity;

        Strategy strategy = iStrategyDao.queryStrategyByStrategyId(strategyId);
        if (null == strategy) return StrategyEntity.builder().build();
        strategyEntity = StrategyEntity.builder()
                .strategyId(strategy.getStrategyId())
                .strategyDesc(strategy.getStrategyDesc())
                .ruleModels(strategy.getRuleModels())
                .build();

        iRedisService.setValue(cacheKey,strategyEntity);
        return strategyEntity;
    }

    @Override
    public StrategyRuleEntity queryStrategyRule(Long strategyId,String ruleWeight) {
        StrategyRule strategyRule = new StrategyRule();
        strategyRule.setStrategyId(strategyId);
        strategyRule.setRuleModel(ruleWeight);
        StrategyRule strategyRuleRes=iStrategyRuleDao.queryStrategyRule(strategyRule);

        return StrategyRuleEntity.builder()
                .strategyId(strategyRuleRes.getStrategyId())
                .ruleModel(strategyRuleRes.getRuleModel())
                .ruleDesc(strategyRuleRes.getRuleDesc())
                .ruleType(strategyRuleRes.getRuleType())
                .ruleValue(strategyRuleRes.getRuleValue())
                .awardId(strategyRuleRes.getAwardId())
                .build();
    }

    @Override
    public String queryStrategyRuleValue(Long strategyId, Integer awardId, String ruleModel) {
        StrategyRule strategyRule = new StrategyRule();
        strategyRule.setStrategyId(strategyId);
        strategyRule.setAwardId(awardId);
        strategyRule.setRuleModel(ruleModel);
        return iStrategyDao.queryStrategyRuleValue(strategyRule);
    }

    @Override
    public StrategyAwardRuleModelVO queryStrategyAwardRuleModel(Long strategyId, Integer awardId) {
        StrategyAward strategyAward = new StrategyAward();
        strategyAward.setStrategyId(strategyId);
        strategyAward.setAwardId(awardId);
        String ruleModels=iStrategyAwardDao.queryStrategyAwardRuleModel(strategyAward);
        return StrategyAwardRuleModelVO.builder().ruleModels(ruleModels).build();
    }

    @Override
    public String queryStrategyRuleValue(Long strategy, String ruleModel) {
        return queryStrategyRuleValue(strategy,null,ruleModel);
    }

}
