package org.cxq.trigger.api;

import org.cxq.trigger.api.dto.ActivityDrawRequestDto;
import org.cxq.trigger.api.dto.ActivityDrawResponseDTO;
import org.cxq.types.model.Response;

/**
 * 抽奖活动服务
 */

public interface IRaffleActivityService {

    /**
     * 活动装配，数据预热缓存
     * @param activityId
     * @return
     */
    Response<Boolean> armory(Long activityId);

    /**
     * 活动抽奖接口
     * @param request
     * @return
     */
    Response<ActivityDrawResponseDTO> draw(ActivityDrawRequestDto request);
}
