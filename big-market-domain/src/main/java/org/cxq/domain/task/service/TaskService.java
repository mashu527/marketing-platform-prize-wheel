package org.cxq.domain.task.service;

import org.cxq.domain.task.model.entity.TaskEntity;
import org.cxq.domain.task.repository.ITaskRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 消息任务服务接口实现
 */


@Service
public class TaskService implements ITaskService{

    @Resource
    private ITaskRepository repository;

    @Override
    public List<TaskEntity> queryNoSendMessageTaskList() {
        return repository.queryNoSendMessageTaskList();
    }

    @Override
    public void sendQMessage(TaskEntity taskEntity) {
        repository.sendQMessage(taskEntity);
    }

    @Override
    public void updateTaskSendMessageCompleted(String userId, String messageId) {
        repository.updateTaskSendMessageCompleted(userId,messageId);
    }

    @Override
    public void updateTaskSendMessageFailed(String userId, String messageId) {
        repository.updateTaskSendMessageFailed(userId,messageId);
    }
}
