package org.cxq.test.infrastructure;


import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.cxq.infrastructure.persistent.dao.IRaffleActivityOrderDao;
import org.cxq.infrastructure.persistent.po.RaffleActivityOrder;
import org.jeasy.random.EasyRandom;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;


@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class RaffleActivityOrderDaoTest {

    @Resource
    private IRaffleActivityOrderDao iRaffleActivityOrderDao;

    private final EasyRandom easyRandom=new EasyRandom();

    @Test
    public void test_insertActivityOrder(){
        for (int i = 0; i < 5; i++) {
            RaffleActivityOrder raffleActivityOrder = new RaffleActivityOrder();
            // EasyRandom 可以通过指定对象类的方式，随机生成对象值。如；easyRandom.nextObject(String.class)、easyRandom.nextObject(RaffleActivityOrder.class)
            raffleActivityOrder.setUserId(easyRandom.nextObject(String.class));
            raffleActivityOrder.setActivityId(100301L);
            raffleActivityOrder.setActivityName("测试活动");
            raffleActivityOrder.setStrategyId(100006L);
            raffleActivityOrder.setOrderId(RandomStringUtils.randomNumeric(12));
            raffleActivityOrder.setOrderTime(new Date());
            raffleActivityOrder.setState("not_used");
            // 插入数据
            iRaffleActivityOrderDao.insert(raffleActivityOrder);
        }
    }


    @Test
    public void test(){
        List<RaffleActivityOrder> orderList = iRaffleActivityOrderDao.queryRaffleActivityOrderByUserId("xiaofuge");
        log.info("订单表查询信息:{}", JSON.toJSONString(orderList));
    }
}
