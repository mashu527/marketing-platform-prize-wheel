package org.cxq.infrastructure.persistent.repository;



import lombok.extern.slf4j.Slf4j;
import org.cxq.domain.strategy.model.entity.StrategyAwardEntity;
import org.cxq.domain.strategy.model.entity.StrategyEntity;
import org.cxq.domain.strategy.model.entity.StrategyRuleEntity;
import org.cxq.domain.strategy.model.vo.*;
import org.cxq.domain.strategy.repository.IStrategyRepository;
import org.cxq.infrastructure.persistent.dao.*;
import org.cxq.infrastructure.persistent.po.*;
import org.cxq.infrastructure.persistent.redis.IRedisService;
import org.cxq.types.common.Constants;
import org.cxq.types.enums.ResponseCode;
import org.cxq.types.exception.AppException;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RMap;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 策略仓储实现
 */
@Slf4j
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
    @Resource
    private IRuleTreeDao iRuleTreeDao;
    @Resource
    private IRuleTreeNodeDao iRuleTreeNodeDao;
    @Resource
    private IRuleTreeNodeLineDao iRuleTreeNodeLineDao;
    @Resource
    private IRaffleActivityDao iRaffleActivityDao;

    @Override
    public List<StrategyAwardEntity> queryStrategyAwardList(Long strategyId) {
        String cacheKey=Constants.RedisKey.STRATEGY_AWARD_LIST_KEY+ strategyId;

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
                    .awardTitle(strategyAward.getAwardTitle())
                    .awardSubTitle(strategyAward.getAwardSubtitle())
                    .sort(strategyAward.getSort())
                    .build();
            strategyAwardEntities.add(strategyAwardEntity);
        }

        iRedisService.setValue(cacheKey,strategyAwardEntities);
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
        return getRateRange(String.valueOf(strategyId));
    }

    @Override
    public int getRateRange(String key) {
        String cacheKey=Constants.RedisKey.STRATEGY_RATE_RANGE_KEY+key;
        if(!iRedisService.isExists(cacheKey)){
            throw new AppException(ResponseCode.UN_ASSEMBLED_STRATEGY_ARMORY.getCode());
        }
        return iRedisService.getValue(cacheKey);
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


    /**
     *
     * @param treeId
     * @return
     */
    @Override
    public RuleTreeVO queryRuleTreeVOByTreeId(String treeId) {
        // 优先从缓存获取
        String cacheKey = Constants.RedisKey.RULE_TREE_VO_KEY + treeId;
        RuleTreeVO ruleTreeVOCache = iRedisService.getValue(cacheKey);
        if (null != ruleTreeVOCache) return ruleTreeVOCache;

        // 从数据库获取
        RuleTree ruleTree = iRuleTreeDao.queryRuleTreeByTreeId(treeId);
        List<RuleTreeNode> ruleTreeNodes = iRuleTreeNodeDao.queryRuleTreeNodeListByTreeId(treeId);
        List<RuleTreeNodeLine> ruleTreeNodeLines = iRuleTreeNodeLineDao.queryRuleTreeNodeLineListByTreeId(treeId);

        // 1. tree node line 转换Map结构
        Map<String, List<RuleTreeNodeLineVO>> ruleTreeNodeLineMap = new HashMap<>();
        for (RuleTreeNodeLine ruleTreeNodeLine : ruleTreeNodeLines) {
            RuleTreeNodeLineVO ruleTreeNodeLineVO = RuleTreeNodeLineVO.builder()
                    .treeId(ruleTreeNodeLine.getTreeId())
                    .ruleNodeFrom(ruleTreeNodeLine.getRuleNodeFrom())
                    .ruleNodeTo(ruleTreeNodeLine.getRuleNodeTo())
                    .ruleLimitType(RuleLimitTypeVO.valueOf(ruleTreeNodeLine.getRuleLimitType()))
                    .ruleLimitValue(RuleLogicCheckTypeVO.valueOf(ruleTreeNodeLine.getRuleLimitValue()))
                    .build();

            List<RuleTreeNodeLineVO> ruleTreeNodeLineVOList = ruleTreeNodeLineMap.computeIfAbsent(ruleTreeNodeLine.getRuleNodeFrom(), k -> new ArrayList<>());
            ruleTreeNodeLineVOList.add(ruleTreeNodeLineVO);
        }

        // 2. tree node 转换为Map结构
        Map<String, RuleTreeNodeVO> treeNodeMap = new HashMap<>();
        for (RuleTreeNode ruleTreeNode : ruleTreeNodes) {
            RuleTreeNodeVO ruleTreeNodeVO = RuleTreeNodeVO.builder()
                    .treeId(ruleTreeNode.getTreeId())
                    .ruleKey(ruleTreeNode.getRuleKey())
                    .ruleDesc(ruleTreeNode.getRuleDesc())
                    .ruleValue(ruleTreeNode.getRuleValue())
                    .treeNodeLineVOList(ruleTreeNodeLineMap.get(ruleTreeNode.getRuleKey()))
                    .build();
            treeNodeMap.put(ruleTreeNode.getRuleKey(), ruleTreeNodeVO);
        }

        // 3. 构建 Rule Tree
        RuleTreeVO ruleTreeVODB = RuleTreeVO.builder()
                .treeId(ruleTree.getTreeId())
                .treeName(ruleTree.getTreeName())
                .treeDesc(ruleTree.getTreeDesc())
                .treeRootRuleNode(ruleTree.getTreeRootRuleKey())
                .treeNodeVOMap(treeNodeMap)
                .build();

        iRedisService.setValue(cacheKey, ruleTreeVODB);
        return ruleTreeVODB;
    }

    @Override
    public void cacheStrategyAwardCount(String cacheKey, Integer awardCount) {
        if (iRedisService.isExists(cacheKey)) return;
        iRedisService.setAtomicLong(cacheKey,awardCount);
    }

    @Override
    public Boolean subtractionAwardStock(String cacheKey) {
        long surplus = iRedisService.decr(cacheKey);
        if(surplus<0){
            iRedisService.setValue(cacheKey,0);
            return false;
        }
        // 1. 按照cacheKey decr 后的值，如 99、98、97 和 key 组成为库存锁的key进行使用。
        // 2. 加锁为了兜底，如果后续有恢复库存，手动处理等，也不会超卖。因为所有的可用库存key，都被加锁了。
        String lockKey=cacheKey + Constants.UNDERLINE + surplus;
        Boolean lock = iRedisService.setNx(lockKey);
        if(!lock){
            log.info("策略奖品库存加锁失败{}",lockKey);
        }
        return lock;
    }

    @Override
    public void awardStockConsumeSendQueue(StrategyAwardStockKeyVO strategyAwardStockKeyVO) {
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_COUNT_QUEUE_KEY;
        RBlockingQueue<StrategyAwardStockKeyVO> blockingQueue = iRedisService.getBlockingQueue(cacheKey);
        RDelayedQueue<StrategyAwardStockKeyVO> delayedQueue = iRedisService.getDelayedQueue(blockingQueue);
        delayedQueue.offer(strategyAwardStockKeyVO,3, TimeUnit.SECONDS);
    }

    @Override
    public StrategyAwardStockKeyVO takeQueueValuee() {
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_COUNT_QUEUE_KEY;
        RBlockingQueue<StrategyAwardStockKeyVO> destinationQueue = iRedisService.getBlockingQueue(cacheKey);
        return destinationQueue.poll();
    }

    @Override
    public void updateStrategyAwardStock(Long strategyId, Integer awardId) {
        StrategyAward strategyAward = new StrategyAward();
        strategyAward.setStrategyId(strategyId);
        strategyAward.setAwardId(awardId);
        iStrategyAwardDao.updateStrategyAwardStock(strategyAward);
    }

    @Override
    public StrategyAwardEntity queryStrategyAwardEntity(Long strategyId, Integer awardId) {
        String cacheKey=Constants.RedisKey.STRATEGY_AWARD_KEY+strategyId+Constants.UNDERLINE+awardId;
        StrategyAwardEntity strategyAwardEntity  = iRedisService.getValue(cacheKey);
        if (null != strategyAwardEntity) return strategyAwardEntity;

        // 查询数据
        StrategyAward strategyAwardReq = new StrategyAward();
        strategyAwardReq.setStrategyId(strategyId);
        strategyAwardReq.setAwardId(awardId);
        StrategyAward strategyAwardRes = iStrategyAwardDao.queryStrategyAward(strategyAwardReq);

        // 转换数据
        strategyAwardEntity = StrategyAwardEntity.builder()
                .strategyId(strategyAwardRes.getStrategyId())
                .awardId(strategyAwardRes.getAwardId())
                .awardTitle(strategyAwardRes.getAwardTitle())
                .awardSubTitle(strategyAwardRes.getAwardSubtitle())
                .awardCount(strategyAwardRes.getAwardCount())
                .awardCountSurplus(strategyAwardRes.getAwardCountSurplus())
                .awardRate(strategyAwardRes.getAwardRate())
                .sort(strategyAwardRes.getSort())
                .build();

        // 缓存结果
        iRedisService.setValue(cacheKey, strategyAwardEntity);
        // 返回数据
        return strategyAwardEntity;
    }

    @Override
    public Long queryStrategyIdByActivityId(Long activityId) {
        return iRaffleActivityDao.queryStrategyIdByActivityId(activityId);
    }

}
