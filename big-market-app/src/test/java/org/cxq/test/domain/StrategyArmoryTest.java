package org.cxq.test.domain;


import lombok.extern.slf4j.Slf4j;
import org.cxq.domain.strategy.service.armory.IStrategyArmory;
import org.cxq.domain.strategy.service.armory.IStrategyDispatch;
import org.cxq.infrastructure.persistent.dao.IStrategyAwardDao;
import org.cxq.infrastructure.persistent.dao.IStrategyDao;
import org.cxq.infrastructure.persistent.po.StrategyAward;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class StrategyArmoryTest {
    @Resource
    private IStrategyDispatch iStrategyDispatch;
    @Resource
    private IStrategyAwardDao iStrategyAwardDao;
    @Resource
    private IStrategyArmory iStrategyArmory;
    @Resource
    private IStrategyDao iStrategyDao;
    @Test
    public void test01(){
        iStrategyArmory.assembleLotteryStrategy(100006L);
    }

    @Test
    public void test02(){
        Integer randomAwardId = iStrategyDispatch.getRandomAwardId(100002L);
        log.info("奖品名称id:{}",randomAwardId);
    }

    @Test
    public void test03(){
        List<StrategyAward> strategyAwards = iStrategyAwardDao.queryStrategyAwardList();
        System.out.println(strategyAwards);
    }


    @Test
    public void test_getRandomAwardId_ruleWeightValue(){
        log.info("测试结果:{} - 4000 策略配置",iStrategyDispatch.getRandomAwardId(100002L,"4000:102,103,104,105"));
    }

    @Test
    public void queryStrategyByStrategyId(){
        log.info("策略信息:{}", iStrategyDao.queryStrategyByStrategyId(100002L));
    }
}
