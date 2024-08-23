package org.cxq.domain.award.model.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.cxq.domain.award.event.SendAwardMessageEvent;
import org.cxq.domain.award.model.valobj.AwardStateVO;
import org.cxq.types.event.BaseEvent;


/**
 * 任务实体对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskEntity {
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 消息主题
     */
    private String topic;
    /**
     * 消息编号
     */
    private String messageId;
    /**
     * 消息主体
     */
    private BaseEvent.EventMessage<SendAwardMessageEvent.SendAwardMessage> message;
    /**
     * 任务状态
     */
    private AwardStateVO state;
}
