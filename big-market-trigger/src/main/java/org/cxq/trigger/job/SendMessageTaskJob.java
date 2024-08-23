package org.cxq.trigger.job;


import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import lombok.extern.slf4j.Slf4j;
import org.cxq.domain.task.model.entity.TaskEntity;
import org.cxq.domain.task.repository.ITaskRepository;
import org.cxq.domain.task.service.ITaskService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 发送MQ消息任务队列
 */

@Slf4j
@Component
public class SendMessageTaskJob {


    @Resource
    private ThreadPoolExecutor executor;
    @Resource
    private ITaskService taskService;
    @Resource
    private IDBRouterStrategy idbRouter;

    @Scheduled(cron="0/5 * * * * ?")
    public void exec(){
        try {
            //获取分库数量
            int dbCount = idbRouter.dbCount();

            //逐个库扫描表【每个库一个任务表】
            for(int i=1;i<=dbCount;i++){

                int finalI = i;

                executor.execute(()-> {
                        try {
                            idbRouter.setDBKey(finalI);
                            idbRouter.setTBKey(0);
                            List<TaskEntity> taskEntities = taskService.queryNoSendMessageTaskList();
                            if(taskEntities.isEmpty()) return;

                            for (TaskEntity taskEntity : taskEntities) {
                                executor.execute(()->{
                                    try {
                                        taskService.sendQMessage(taskEntity);
                                        taskService.updateTaskSendMessageCompleted(taskEntity.getUserId(),taskEntity.getMessageId());
                                    } catch (Exception e) {
                                        log.error("定时任务，发送MQ消息失败 userId: {} topic: {}", taskEntity.getUserId(), taskEntity.getTopic());
                                        taskService.updateTaskSendMessageFailed(taskEntity.getUserId(), taskEntity.getMessageId());
                                    }
                                });
                            }
                        } finally {
                            idbRouter.clear();
                        }
                });
            }
        } catch (Exception e) {
            log.error("定时任务，扫描MQ任务表发送消息失败。", e);
        }
    }
}
