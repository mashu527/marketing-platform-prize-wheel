package org.cxq.trigger.listener;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.cxq.domain.activity.service.IRaffleActivitySkuStockService;
import org.cxq.types.event.BaseEvent;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


/**
 *  消息队列监听活动sku库存消费为0
 */
@Slf4j
@Component
public class ActivitySkuStockZeroCustomer {

    @Value("${spring.rabbitmq.topic.activity_sku_stock_zero}")
    private String topic;

    @Resource
    private IRaffleActivitySkuStockService iSkuStock;

    @RabbitListener(queuesToDeclare = @Queue(value="activity_sku_stock_zero"))
    public void listenber(String message){
        try {
            log.info("监听活动sku库存消耗为0消息 topic:{} message:{}",topic,message);
            BaseEvent.EventMessage<Long> eventMessage = JSON.parseObject(message, new TypeReference<BaseEvent.EventMessage<Long>>() {
            }.getType());

            Long sku = eventMessage.getData();

            //更新库存
            iSkuStock.clearActivitySkuStock(sku);

            //清空队列（此时不许眼延迟更新数据库）
            iSkuStock.clearQueueValue();
        } catch (Exception e) {
            log.info("监听活动sku库存消耗为0消息,消费失败 topic:{} mewssage:{}",topic,message);
        }
    }
}
