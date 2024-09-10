package org.cxq.trigger.http;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.cxq.domain.activity.model.entity.UserRaffleOrderEntity;
import org.cxq.domain.activity.service.IRaffleActivityPartakeService;
import org.cxq.domain.activity.service.armory.IActivityArmory;
import org.cxq.domain.award.model.entity.UserAwardRecordEntity;
import org.cxq.domain.award.model.valobj.AwardStateVO;
import org.cxq.domain.award.service.IAwardService;
import org.cxq.domain.strategy.model.entity.RaffleAwardEntity;
import org.cxq.domain.strategy.model.entity.RaffleFactorEntity;
import org.cxq.domain.strategy.service.IRaffleStrategy;
import org.cxq.domain.strategy.service.armory.IStrategyArmory;
import org.cxq.trigger.api.IRaffleActivityService;
import org.cxq.trigger.api.dto.ActivityDrawRequestDto;
import org.cxq.trigger.api.dto.ActivityDrawResponseDTO;
import org.cxq.types.enums.ResponseCode;
import org.cxq.types.exception.AppException;
import org.cxq.types.model.Response;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 抽奖活动服务
 */

@RestController()
@RequestMapping("api/${app.config.api-version}/raffle/activity")
@Slf4j
@CrossOrigin("${app.config.cross-origin}")
public class RaffleActivityController implements IRaffleActivityService {

    @Resource
    private IActivityArmory activityArmory;
    @Resource
    private IStrategyArmory strategyArmory;
    @Resource
    private IRaffleStrategy raffleStrategy;
    @Resource
    private IRaffleActivityPartakeService raffleActivityPartakeService;
    @Resource
    private IAwardService awardService;

    @RequestMapping(value = "armory",method = RequestMethod.GET)
    @Override
    public Response<Boolean> armory(@RequestParam Long activityId) {
        try {
            log.info("活动装配，数据预热,开始 activityId:{}",activityId);
            //1.活动装配
            activityArmory.assembleActivitySkuByActivityId(activityId);
            //2.策略装配
            strategyArmory.assembleLotteryStrategyByActivityId(activityId);

            Response<Boolean> response = Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(true)
                    .build();

            log.info("活动装配，数据预热，完成 activityId:{}",activityId);
            return response;
        } catch (Exception e) {
            log.error("活动装配,数据预热,失败 activityId:{}",activityId,e);
            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .data(false)
                    .build();
        }

    }

    @RequestMapping(value = "draw",method = RequestMethod.POST)
    @Override
    public Response<ActivityDrawResponseDTO> draw(@RequestBody ActivityDrawRequestDto request) {
        try {
            log.info("活动抽奖开始 userId:{} activity:{}",request.getUserId(),request.getActivityId());

            //1.参数校验
            if(StringUtils.isBlank(request.getUserId()) || request.getActivityId()==null){
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(),ResponseCode.ILLEGAL_PARAMETER.getInfo());
            }

            //2.参与活动 - 创建参与记录订单
            UserRaffleOrderEntity orderEntity=raffleActivityPartakeService.createOrder(request.getUserId(),request.getActivityId());
            log.info("活动抽奖开始,创建订单 userId:{} activityId:{} orderId:{}",orderEntity.getUserId(),orderEntity.getActivityId(),orderEntity.getOrderId());

            //3.抽奖策略 - 执行抽奖
            RaffleAwardEntity raffleAwardEntity = raffleStrategy.performRaffle(RaffleFactorEntity.builder()
                    .userId(orderEntity.getUserId())
                    .strategyId(orderEntity.getStrategyId())
                    .build());


            //4.存放结果 - 写入中奖记录
            UserAwardRecordEntity userAwardRecord = UserAwardRecordEntity.builder()
                    .userId(orderEntity.getUserId())
                    .activityId(orderEntity.getActivityId())
                    .strategyId(orderEntity.getStrategyId())
                    .orderId(orderEntity.getOrderId())
                    .awardId(raffleAwardEntity.getAwardId())
                    .awardTitle(raffleAwardEntity.getAwardTitle())
                    .awardTime(new Date())
                    .awardState(AwardStateVO.create)
                    .build();

            //写入中奖记录
            awardService.saveUserAwardRecord(userAwardRecord);

            //5/返回结果
            return Response.<ActivityDrawResponseDTO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(ActivityDrawResponseDTO
                            .builder()
                            .awardId(raffleAwardEntity.getAwardId())
                            .awardTitle(raffleAwardEntity.getAwardTitle())
                            .awardIndex(raffleAwardEntity.getSort())
                            .build())
                    .build();
        } catch (AppException e) {
            log.error("活动抽奖失败 userId:{} activityId:{}",request.getUserId(),request.getActivityId(),e);
            return Response.<ActivityDrawResponseDTO>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        }catch (Exception e) {
            log.error("活动抽奖失败 userId:{} activityId:{}",request.getUserId(),request.getActivityId(),e);
            return Response.<ActivityDrawResponseDTO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }
}
