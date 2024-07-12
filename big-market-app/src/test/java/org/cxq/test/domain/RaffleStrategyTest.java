package org.cxq.test.domain;


import lombok.extern.slf4j.Slf4j;
import org.cxq.domain.strategy.model.entity.RaffleAwardEntity;
import org.cxq.domain.strategy.model.entity.RaffleFactorEntity;
import org.cxq.domain.strategy.service.IRaffleStrategy;
import org.cxq.domain.strategy.service.raffle.DefaultRaffleStrategy;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class RaffleStrategyTest {
    @Resource
    private IRaffleStrategy iRaffleStrategy;

    @Test
    public void test_performRaffle(){
        RaffleAwardEntity raffleAwardEntity = iRaffleStrategy.performRaffle(RaffleFactorEntity.builder()
                .userId("user001")
                .strategyId(100002L)
                .build());

        log.info("抽奖奖品:{}",raffleAwardEntity);
    }
}
