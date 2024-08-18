package org.cxq.domain.activity.service.partake;

import lombok.var;
import org.apache.commons.lang3.RandomStringUtils;
import org.cxq.domain.activity.model.aggregate.CreatePartakeOrderAggregate;
import org.cxq.domain.activity.model.aggregate.CreateQuotaOrderAggregate;
import org.cxq.domain.activity.model.entity.*;
import org.cxq.domain.activity.model.valobj.UserRaffleOrderStateVO;
import org.cxq.domain.activity.repository.IActivityRepository;
import org.cxq.types.enums.ResponseCode;
import org.cxq.types.exception.AppException;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class RaffleActivityPartakeService extends AbstractRaffleActivityPartake{

    private final SimpleDateFormat dateFormatMonth=new SimpleDateFormat("yyyy-MM");
    private final SimpleDateFormat dateFormatDay=new SimpleDateFormat("yyyy-MM-dd");

    public RaffleActivityPartakeService(IActivityRepository iActivityRepository) {
        super(iActivityRepository);
    }

    @Override
    public UserRaffleOrderEntity buildRaffleOrder(String userId, Long activityId, Date date) {
        ActivityEntity activityEntity = iActivityRepository.queryRaffleActivityByActivityId(activityId);

        UserRaffleOrderEntity userRaffleOrderEntity=new UserRaffleOrderEntity();
        userRaffleOrderEntity.setUserId(userId);
        userRaffleOrderEntity.setActivityId(activityId);
        userRaffleOrderEntity.setOrderTime(date);
        userRaffleOrderEntity.setActivityName(activityEntity.getActivityName());
        userRaffleOrderEntity.setOrderId(RandomStringUtils.randomNumeric(12));
        userRaffleOrderEntity.setStrategyId(activityEntity.getStrategyId());
        userRaffleOrderEntity.setOrderState(UserRaffleOrderStateVO.create);

        return userRaffleOrderEntity;
    }

    @Override
    public CreatePartakeOrderAggregate doFilterAccount(String userId, Long activityId, Date date) {
        //查询总额度账户
        ActivityAccountEntity activityAccountEntity=iActivityRepository.queryActivityAccountByUserId(userId,activityId);

        //判断总额度剩余
        if(activityAccountEntity==null || activityAccountEntity.getTotalCountSurplus()<=0){
            throw new AppException(ResponseCode.ACCOUNT_QUOTA_ERRROR.getCode(),ResponseCode.ACCOUNT_QUOTA_ERRROR.getInfo());
        }

        String month = dateFormatMonth.format(date);
        String day = dateFormatDay.format(date);

        //插叙月账户额度
        ActivityAccountMonthEntity activityAccountMonthEntity=iActivityRepository.queryActivityAccountMonthByUserId(userId,activityId,month);
        if(activityAccountMonthEntity!=null && activityAccountMonthEntity.getMonthCountSurplus()<=0){
            throw new AppException(ResponseCode.ACCOUNT_MONTH_QUOTA_ERROR.getCode(),ResponseCode.ACCOUNT_MONTH_QUOTA_ERROR.getInfo());
        }

        // 创建月账户额度；true = 存在月账户、false = 不存在月账户
        boolean isExistAccountMonth = null != activityAccountMonthEntity;
        if(activityAccountMonthEntity==null){
            activityAccountMonthEntity = new ActivityAccountMonthEntity();
            activityAccountMonthEntity.setUserId(userId);
            activityAccountMonthEntity.setActivityId(activityId);
            activityAccountMonthEntity.setMonthCount(activityAccountEntity.getMonthCount());
            activityAccountMonthEntity.setMonth(month);
            activityAccountMonthEntity.setMonthCountSurplus(activityAccountEntity.getMonthCountSurplus());
        }


        // 查询日账户额度
        ActivityAccountDayEntity activityAccountDayEntity = iActivityRepository.queryActivityAccountDayByUserId(userId, activityId, day);
        if (null != activityAccountDayEntity && activityAccountDayEntity.getDayCountSurplus() <= 0) {
            throw new AppException(ResponseCode.ACCOUNT_DAY_QUOTA_ERROR.getCode(), ResponseCode.ACCOUNT_DAY_QUOTA_ERROR.getInfo());
        }


        // 创建日账户额度；true = 存在月账户、false = 不存在月账户
        boolean isExistAccountDay = null != activityAccountDayEntity;
        if (null == activityAccountDayEntity) {
            activityAccountDayEntity = new ActivityAccountDayEntity();
            activityAccountDayEntity.setUserId(userId);
            activityAccountDayEntity.setActivityId(activityId);
            activityAccountDayEntity.setDay(day);
            activityAccountDayEntity.setDayCount(activityAccountEntity.getDayCount());
            activityAccountDayEntity.setDayCountSurplus(activityAccountEntity.getDayCountSurplus());
        }


        CreatePartakeOrderAggregate createPartakeOrderAggregate = new CreatePartakeOrderAggregate();
        createPartakeOrderAggregate.setUserId(userId);
        createPartakeOrderAggregate.setActivityId(activityId);
        createPartakeOrderAggregate.setActivityAccountEntity(activityAccountEntity);
        createPartakeOrderAggregate.setExistAccountDay(isExistAccountDay);
        createPartakeOrderAggregate.setActivityAccountDayEntity(activityAccountDayEntity);
        createPartakeOrderAggregate.setExistAccountMonth(isExistAccountMonth);
        createPartakeOrderAggregate.setActivityAccountMonthEntity(activityAccountMonthEntity);

        return createPartakeOrderAggregate;
    }

}
