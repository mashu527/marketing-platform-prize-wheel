package org.cxq.domain.strategy.model.entity;

import lombok.*;
import org.checkerframework.checker.index.qual.NegativeIndexFor;
import org.cxq.domain.strategy.model.vo.RuleLogicCheckTypeVO;

/**
 * 规则动作实体
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RuleActionEntity<T extends RuleActionEntity.RaffleEntity> {

    private String code= RuleLogicCheckTypeVO.ALLOW.getCode();
    private String info= RuleLogicCheckTypeVO.TAKE_OVER.getInfo();
    private String ruleModel;
    private T data;

    static public class RaffleEntity{

    }


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    static public class RaffleBeforeEntity extends RaffleEntity{
        /**
         * 策略ID
         */
        private Long strategyId;

        /**
         * 权重值Key；用于抽奖时可以选择权重抽奖。
         */
        private String ruleWeightValueKey;

        /**
         * 奖品ID；
         */
        private Integer awardId;
    }

    static public class RaffleCenterEntity extends RaffleEntity{}

    static public class RaffleAfterEntity extends RaffleEntity{}
}
