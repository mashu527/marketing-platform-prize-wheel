package org.cxq.domain.task.service;

import org.cxq.domain.task.model.entity.TaskEntity;

import java.util.List;

/**
 * 消息服务接口
 */

public interface ITaskService {
    List<TaskEntity> queryNoSendMessageTaskList();

    void sendQMessage(TaskEntity taskEntity);

    void updateTaskSendMessageCompleted(String userId,String messageId);

    void updateTaskSendMessageFailed(String userId,String messageId);
}
