package org.cxq.domain.activity.service.partake;

import com.alibaba.fastjson.JSON;
import com.google.j2objc.annotations.ReflectionSupport;
import lombok.extern.slf4j.Slf4j;
import org.cxq.domain.activity.model.aggregate.CreatePartakeOrderAggregate;
import org.cxq.domain.activity.model.entity.ActivityEntity;
import org.cxq.domain.activity.model.entity.PartakeRaffleActivityEntity;
import org.cxq.domain.activity.model.entity.UserRaffleOrderEntity;
import org.cxq.domain.activity.model.valobj.ActivityStateVO;
import org.cxq.domain.activity.repository.IActivityRepository;
import org.cxq.domain.activity.service.IRaffleActivityPartakeService;
import org.cxq.types.enums.ResponseCode;
import org.cxq.types.exception.AppException;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 抽奖活动参与类
 */

@Slf4j
public abstract class AbstractRaffleActivityPartake implements IRaffleActivityPartakeService {

    protected final IActivityRepository iActivityRepository;


    public AbstractRaffleActivityPartake(IActivityRepository iActivityRepository) {
        this.iActivityRepository = iActivityRepository;
    }

    @Override
    public UserRaffleOrderEntity createOrder(PartakeRaffleActivityEntity partakeRaffleActivityEntity) {
        //基本信息
        String userId = partakeRaffleActivityEntity.getUserId();
        Long activityId = partakeRaffleActivityEntity.getActivityId();
        Date date = new Date();

        //活动查询
        ActivityEntity activityEntity = iActivityRepository.queryRaffleActivityByActivityId(activityId);

        //活动校验
        if(!activityEntity.getState().equals(ActivityStateVO.open)){
            throw new AppException(ResponseCode.ACTIVITY_STATE_ERROR.getCode(),ResponseCode.ACTIVITY_STATE_ERROR.getInfo());
        }

        if(activityEntity.getBeginDateTime().after(date) || activityEntity.getEndDateTime().before(date)){
            throw new AppException(ResponseCode.ACTIVITY_DATE_ERROR.getCode(),ResponseCode.ACTIVITY_DATE_ERROR.getInfo());
        }

        //查询未被使用的活动参与订单记录
        UserRaffleOrderEntity userRaffleOrderEntity = iActivityRepository.queryNoUsedRaffleOrder(partakeRaffleActivityEntity);
        if(userRaffleOrderEntity!=null){
            log.info("创建参与活动订单 userId:{} activityId:{} userRaffleOrderEntity:{}", userId, activityId, JSON.toJSONString(userRaffleOrderEntity));
            return userRaffleOrderEntity;
        }

        //账户额度过滤&返回账户构建对象
        CreatePartakeOrderAggregate createPartakeOrderAggregate=this.doFilterAccount(userId,activityId,date);

        //构建订单
        UserRaffleOrderEntity userRaffleOrder=this.buildRaffleOrder(userId,activityId,date);

        //填充实体订单对象
        createPartakeOrderAggregate.setUserRaffleOrderEntity(userRaffleOrder);

        iActivityRepository.saveCreatePartakeOrderAggregate(createPartakeOrderAggregate);

        return userRaffleOrder;
    }

    public abstract UserRaffleOrderEntity buildRaffleOrder(String userId, Long activityId, Date date);

    public abstract CreatePartakeOrderAggregate doFilterAccount(String userId, Long activityId, Date date);
}
