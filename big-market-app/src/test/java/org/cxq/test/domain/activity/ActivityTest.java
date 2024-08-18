package org.cxq.test.domain.activity;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.cxq.domain.activity.model.entity.PartakeRaffleActivityEntity;
import org.cxq.domain.activity.model.entity.SkuRechargeEntity;
import org.cxq.domain.activity.model.entity.UserRaffleOrderEntity;
import org.cxq.domain.activity.service.IRaffleActivityAccountQuotaService;
import org.cxq.domain.activity.service.IRaffleActivityPartakeService;
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
    private IRaffleActivityAccountQuotaService iRaffleOrder;

    @Resource
    private IActivityArmory iActivityArmory;
    @Resource
    private IRaffleActivityPartakeService iRaffleActivityPartakeService;

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


    @Test
    public void testPartakeService(){
        PartakeRaffleActivityEntity partakeRaffleActivityEntity = new PartakeRaffleActivityEntity().builder()
                .userId("xiaofuge")
                .activityId(100301L).build();

        UserRaffleOrderEntity order = iRaffleActivityPartakeService.createOrder(partakeRaffleActivityEntity);
        log.info("生成用户抽奖订单:{}",order);
    }
}
