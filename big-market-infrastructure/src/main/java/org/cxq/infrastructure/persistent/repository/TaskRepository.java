package org.cxq.infrastructure.persistent.repository;

import org.cxq.domain.task.model.entity.TaskEntity;
import org.cxq.domain.task.repository.ITaskRepository;
import org.cxq.infrastructure.event.EventPublisher;
import org.cxq.infrastructure.persistent.dao.ITaskDao;
import org.cxq.infrastructure.persistent.po.Task;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 任务仓储实现
 */

@Repository
public class TaskRepository implements ITaskRepository {

    @Resource
    private ITaskDao taskDao;
    @Resource
    private EventPublisher eventPublisher;

    @Override
    public List<TaskEntity> queryNoSendMessageTaskList() {
        List<Task> tasks=taskDao.queryNoSendMessageTaskList();
        ArrayList<TaskEntity> taskEntities = new ArrayList<>(tasks.size());
        for (Task task : tasks) {
            TaskEntity taskEntity = new TaskEntity();
            taskEntity.setUserId(task.getUserId());
            taskEntity.setTopic(task.getTopic());
            taskEntity.setMessage(task.getMessage());
            taskEntity.setMessageId(task.getMessageId());
            taskEntities.add(taskEntity);
        }

        return taskEntities;
    }

    @Override
    public void sendQMessage(TaskEntity taskEntity) {
        eventPublisher.publish(taskEntity.getTopic(),taskEntity.getMessage());
    }

    @Override
    public void updateTaskSendMessageCompleted(String userId, String messageId) {
        Task task = new Task();
        task.setUserId(userId);
        task.setMessageId(messageId);
        taskDao.updateTaskSendMessageCompleted(task);
    }

    @Override
    public void updateTaskSendMessageFailed(String userId, String messageId) {
        Task task = new Task();
        task.setUserId(userId);
        task.setMessageId(messageId);
        taskDao.updateTaskSendMessageFail(task);
    }

}
