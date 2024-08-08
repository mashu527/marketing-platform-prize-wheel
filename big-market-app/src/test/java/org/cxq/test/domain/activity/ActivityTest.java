package org.cxq.test.domain.activity;


import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.cxq.domain.activity.model.entity.ActivityOrderEntity;
import org.cxq.domain.activity.model.entity.ActivityShopCartEntity;
import org.cxq.domain.activity.repository.IActivityRepository;
import org.cxq.domain.activity.service.IRaffleOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class ActivityTest {

    @Resource
    private IRaffleOrder iRaffleOrder;

    @Test
    public void test(){
        ActivityShopCartEntity activityShopCartEntity = ActivityShopCartEntity.builder()
                .sku(9011L)
                .userId("cxq").build();
        ActivityOrderEntity raffleActivityOrder = iRaffleOrder.createRaffleActivityOrder(activityShopCartEntity);
        log.info("查询结果:{}", JSON.toJSONString(raffleActivityOrder));
    }

}
