package org.cxq.domain.task.repository;


import org.cxq.domain.task.model.entity.TaskEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 消息任务仓储服务
 */

public interface ITaskRepository {


    List<TaskEntity> queryNoSendMessageTaskList();

    void sendQMessage(TaskEntity taskEntity);

    void updateTaskSendMessageCompleted(String userId, String messageId);

    void updateTaskSendMessageFailed(String userId, String messageId);
}
