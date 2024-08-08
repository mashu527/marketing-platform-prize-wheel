package org.cxq.domain.activity.model.valobj;


import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * 订单状态枚举值
 */
@Getter
public enum OrderStateVO {



    completed("completed","完成");

    OrderStateVO(String code,String desc){
        this.code=code;
        this.desc=desc;
    }

    private final String code;
    private final String desc;
}
