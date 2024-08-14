package org.cxq.test.domain.activity;


import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.cxq.domain.activity.model.entity.SkuRechargeEntity;
import org.cxq.domain.activity.repository.IActivityRepository;
import org.cxq.domain.activity.service.IRaffleOrder;
import org.cxq.domain.activity.service.armory.IActivityArmory;
import org.cxq.types.exception.AppException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class ActivityTest {

    @Resource
    private IRaffleOrder iRaffleOrder;

    @Resource
    private IActivityArmory iActivityArmory;

    @Test
    public void setup(){
        boolean result = iActivityArmory.assembleActivity(9011L);
        log.info("装配结果：{}",result);
    }

    @Test
    public void testCreateRechargeOrder() throws InterruptedException {

        for(int i=0;i<5;i++){
            try {
                SkuRechargeEntity skuRechargeEntity = new SkuRechargeEntity();
                skuRechargeEntity.setUserId("cxq");
                skuRechargeEntity.setOutBusinessNo(RandomStringUtils.randomNumeric(12));
                skuRechargeEntity.setSku(9011L);

                String orderId = iRaffleOrder.createRechargeOrder(skuRechargeEntity);
                log.info("测试结果,OrderId:{}",orderId);
            } catch (AppException e) {
                log.info(e.getInfo());
            }
        }

        new CountDownLatch(1).await();
    }
}
