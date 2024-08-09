package org.cxq.domain.activity.service.rule.factory;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.cxq.domain.activity.service.rule.IActionChain;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DefaultActivityChainFactory {

    private final IActionChain iActionChain;

    /**
     * 1. 通过构造函数注入。
     * 2. Spring 可以自动注入 IActionChain 接口实现类到 map 对象中，key 就是 bean 的名字。
     * 3. 活动下单动作的责任链是固定的，所以直接在构造函数中组装即可。
     */
    public DefaultActivityChainFactory(Map<String, IActionChain> actionChainMap){
        iActionChain = actionChainMap.get(ActionModel.activity_base_action.getCode());
        iActionChain.appendNext(actionChainMap.get(ActionModel.activity_sku_stock_action.getCode()));
    }

    public IActionChain openActionChain() {
        return this.iActionChain;
    }

    @AllArgsConstructor
    @Getter
    public enum ActionModel{
        activity_base_action("activity_base_action","活动的库存、时间校验"),
        activity_sku_stock_action("activity_sku_stock_action","活动sku库存"),
        ;

        private final String code;
        private final String indo;

    }


    
}
