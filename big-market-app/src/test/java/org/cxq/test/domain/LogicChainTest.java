package org.cxq.test.domain;


import lombok.extern.slf4j.Slf4j;
import org.cxq.domain.strategy.service.armory.IStrategyArmory;
import org.cxq.domain.strategy.service.rule.chain.ILogicChain;
import org.cxq.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import org.cxq.domain.strategy.service.rule.chain.impl.RuleWeightLogicChain;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import javax.annotation.Resource;

@SpringBootTest
@Slf4j
@RunWith(SpringRunner.class)
public class LogicChainTest {

    @Resource
    private IStrategyArmory strategyArmory;
    @Resource
    private RuleWeightLogicChain ruleWeightLogicChain;
    @Resource
    private DefaultChainFactory defaultChainFactory;

    @Test
    public void setUp() {
        // 策略装配 100001、100002、100003
//        log.info("测试结果：{}", strategyArmory.assembleLotteryStrategy(100001L));
        log.info("测试结果：{}", strategyArmory.assembleLotteryStrategy(100003L));
//        log.info("测试结果：{}", strategyArmory.assembleLotteryStrategy(100003L));
    }

    @Test
    public void test_LogicChain_rule_blacklist() {
        ILogicChain logicChain = defaultChainFactory.openLogicChain(100003L);
        DefaultChainFactory.StrategyAwardVO strategyAwardVO = logicChain.logic("user001", 100003L);
        log.info("测试结果：{}", strategyAwardVO.getAwardId());
    }

    @Test
    public void test_LogicChain_rule_weight() {
        // 通过反射 mock 规则中的值
        ReflectionTestUtils.setField(ruleWeightLogicChain, "userScore", 4900L);

        ILogicChain logicChain = defaultChainFactory.openLogicChain(100003L);
        DefaultChainFactory.StrategyAwardVO strategyAwardVO = logicChain.logic("xiaofuge", 100003L);
        log.info("测试结果：{}", strategyAwardVO.getAwardId());
    }

    @Test
    public void test_LogicChain_rule_default() {
        ILogicChain logicChain = defaultChainFactory.openLogicChain(100003L);
        DefaultChainFactory.StrategyAwardVO strategyAwardVO = logicChain.logic("xiaofuge", 100003L);
        log.info("测试结果：{}", strategyAwardVO.getAwardId());
    }
}
