package org.cxq.test.domain;


import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.cxq.domain.strategy.model.entity.RaffleAwardEntity;
import org.cxq.domain.strategy.model.entity.RaffleFactorEntity;
import org.cxq.domain.strategy.service.IRaffleStrategy;
import org.cxq.domain.strategy.service.armory.IStrategyArmory;
import org.cxq.domain.strategy.service.raffle.DefaultRaffleStrategy;
import org.cxq.domain.strategy.service.rule.chain.impl.RuleWeightLogicChain;
import org.cxq.domain.strategy.service.rule.impl.RuleWeightLogicFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class RaffleStrategyTest {
    @Resource
    private IRaffleStrategy iRaffleStrategy;
    @Resource
    private IStrategyArmory iStrategyArmory;
    @Resource
    private RuleWeightLogicChain ruleWeightLogicChain;
    @Resource
    private RuleWeightLogicFilter ruleWeightLogicFilter;

    @Test
    public void test_performRaffle() throws InterruptedException {
        for(int i=0;i<3;i++){
            RaffleAwardEntity raffleAwardEntity = iRaffleStrategy.performRaffle(RaffleFactorEntity.builder()
                    .userId("cxq")
                    .strategyId(100006L)
                    .build());

            log.info("抽奖奖品:{}",raffleAwardEntity);
        }

        //等待 UpdateAwardStockJob 消费队列
        new CountDownLatch(1).await();
    }

    @Test
    public void test_raffle_center_rule_lock(){
        RaffleFactorEntity raffleFactorEntity = RaffleFactorEntity.builder()
                .userId("cxq")
                .strategyId(100006L)
                .build();

        RaffleAwardEntity raffleAwardEntity = iRaffleStrategy.performRaffle(raffleFactorEntity);

        log.info("请求参数：{}", JSON.toJSONString(raffleFactorEntity));
        log.info("测试结果：{}", JSON.toJSONString(raffleAwardEntity));
    }
}
