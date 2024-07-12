package org.cxq.domain.strategy.model.entity;

import lombok.Data;

@Data
public class RuleMatterEntity {
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 策略ID
     */
    private Long strategyId;
    /**
     * 奖品ID 【规则类型为策略，则不需要奖品ID】
     */
    private Integer awardId;
    /**
     * 抽奖规则类型【rule_random - 随机值计算、rule_lock - 抽奖几次后解锁、rule_lock_award - 幸运奖(兜底奖品)】
     */
    private String ruleModel;
}
