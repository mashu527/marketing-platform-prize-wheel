package org.cxq.trigger.api.dto;


import lombok.Data;

/**
 *
 * @description 活动抽奖请求对象
 * @create 2024-04-13 09:29
 */

@Data
public class ActivityDrawRequestDto {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 活动ID
     */
    private Long activityId;
}
