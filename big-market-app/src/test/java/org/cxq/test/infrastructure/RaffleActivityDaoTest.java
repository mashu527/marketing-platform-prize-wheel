package org.cxq.test.infrastructure;


import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.cxq.infrastructure.persistent.dao.IRaffleActivityCountDao;
import org.cxq.infrastructure.persistent.dao.IRaffleActivityDao;
import org.cxq.infrastructure.persistent.po.RaffleActivity;
import org.cxq.infrastructure.persistent.po.RaffleActivityCount;
import org.json.JSONString;
import org.junit.Test;
import org.junit.jupiter.api.TestTemplate;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class RaffleActivityDaoTest {

    @Resource
    private IRaffleActivityDao iRaffleActivityDao;

    @Resource
    private IRaffleActivityCountDao iRaffleActivityCountDao;
    @Test
    public void testRaffleActivityDao(){
        RaffleActivity raffleActivity = iRaffleActivityDao.queryRaffleActivityByActivityId(100301L);
        log.info("抽奖活动表:{}", JSON.toJSONString(raffleActivity));
    }

    @Test
    public void testRaffleActivityCountDao(){
        RaffleActivityCount raffleActivityCount = iRaffleActivityCountDao.queryRaffleActivityCountByActivityCountId(11101L);
        log.info("活动次数:{}",JSON.toJSONString(raffleActivityCount));
    }


}
