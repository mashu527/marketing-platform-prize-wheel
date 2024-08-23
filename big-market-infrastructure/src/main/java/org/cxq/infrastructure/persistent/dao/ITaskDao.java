package org.cxq.infrastructure.persistent.dao;

import cn.bugstack.middleware.db.router.annotation.DBRouter;
import org.apache.ibatis.annotations.Mapper;
import org.cxq.domain.task.model.entity.TaskEntity;
import org.cxq.infrastructure.persistent.po.Task;

import java.util.List;

/**
 *
 * @description 任务表，发送MQ
 * @create 2024-04-03 15:57
 */

@Mapper
public interface ITaskDao {
    void insert(Task task);

    @DBRouter
    void updateTaskSendMessageCompleted(Task task);

    @DBRouter
    void updateTaskSendMessageFail(Task task);

    List<Task> queryNoSendMessageTaskList();

}
