package org.cxq.domain.award.service;

import org.cxq.domain.award.event.SendAwardMessageEvent;
import org.cxq.domain.award.model.aggregate.UserAwardRecordAggregate;
import org.cxq.domain.award.model.entity.TaskEntity;
import org.cxq.domain.award.model.entity.UserAwardRecordEntity;
import org.cxq.domain.award.model.valobj.AwardStateVO;
import org.cxq.domain.award.repository.IAwardRepository;
import org.cxq.types.event.BaseEvent;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class AwardService implements IAwardService{

    @Resource
    private IAwardRepository iAwardRepository;

    @Resource
    private SendAwardMessageEvent sendAwardMessageEvent;

    @Override
    public void saveUserAwardRecord(UserAwardRecordEntity userAwardRecordEntity) {
        //构建消息对象
        SendAwardMessageEvent.SendAwardMessage sendAwardMessage = new SendAwardMessageEvent.SendAwardMessage().builder()
                .userId(userAwardRecordEntity.getUserId())
                .awardId(userAwardRecordEntity.getAwardId())
                .awardTitle(userAwardRecordEntity.getAwardTitle()).build();

        BaseEvent.EventMessage<SendAwardMessageEvent.SendAwardMessage> sendAwardMessageEventMessage = sendAwardMessageEvent.buildEventMessage(sendAwardMessage);

        //构建任务对象
        TaskEntity taskEntity = TaskEntity.builder()
                .userId(userAwardRecordEntity.getUserId())
                .topic(sendAwardMessageEvent.topic())
                .message(sendAwardMessageEventMessage)
                .messageId(sendAwardMessageEventMessage.getId())
                .state(AwardStateVO.create)
                .build();

        //构建复合对象
        UserAwardRecordAggregate userAwardRecordAggregate = UserAwardRecordAggregate.builder()
                .userAwardRecordEntity(userAwardRecordEntity)
                .taskEntity(taskEntity)
                .build();

        //存储聚合对象
        iAwardRepository.saveUserAwardRecord(userAwardRecordAggregate);
    }
}
