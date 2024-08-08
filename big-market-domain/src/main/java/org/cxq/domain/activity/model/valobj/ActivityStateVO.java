package org.cxq.domain.activity.model.valobj;


import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * 活动状态值对象
 */
@Getter
@AllArgsConstructor
public enum ActivityStateVO {
    create("create","创建");

    private final String code;
    private final String desc;
}
