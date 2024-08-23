package org.cxq.domain.task.model.entity;


import lombok.Data;

/**
 * 任务实体对象
 */

@Data
public class TaskEntity {
    /**
     * 用户Id
     */
    private String userId;
    /**
     * 消息主题
     */
    private String topic;
    /**
     * 消息Id
     */
    private String messageId;
    /**
     * 消息主体
     */
    private String message;
}
