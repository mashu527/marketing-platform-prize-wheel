package org.cxq.domain.activity.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRaffleOrderStateVO {
    create("create","创建"),
    used("used","已使用"),
    cancel("cancel","已作废"),
    ;

    private final String code;
    private final String info;
}
