package org.cxq.trigger.http;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.cxq.domain.strategy.model.entity.RaffleAwardEntity;
import org.cxq.domain.strategy.model.entity.RaffleFactorEntity;
import org.cxq.domain.strategy.model.entity.StrategyAwardEntity;
import org.cxq.domain.strategy.service.IRaffleAward;
import org.cxq.domain.strategy.service.IRaffleStrategy;
import org.cxq.domain.strategy.service.armory.IStrategyArmory;
import org.cxq.trigger.api.IRaffleStrategyService;
import org.cxq.trigger.api.dto.RaffleAwardListRequestDTO;
import org.cxq.trigger.api.dto.RaffleAwardListResponseDTO;
import org.cxq.trigger.api.dto.RaffleStrategyRequestDTO;
import org.cxq.trigger.api.dto.RaffleStrategyResponseDTO;
import org.cxq.types.enums.ResponseCode;
import org.cxq.types.exception.AppException;
import org.cxq.types.model.Response;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


/**
 * 营销抽奖服务
 */

@RestController()
@Slf4j
@CrossOrigin("${app.config.cross-origin}")
@RequestMapping("/api/${app.config.api-version}/raffle/strategy")
public class RaffleStrategyController implements IRaffleStrategyService {

    @Resource
    private IStrategyArmory iStrategyArmory;

    @Resource
    private IRaffleAward iRaffleAward;

    @Resource
    private IRaffleStrategy iRaffleStrategy;

    @RequestMapping(value = "strategy_armory",method = RequestMethod.GET)
    @Override
    public Response<Boolean> StrategyArmory(Long strategyId) {
        try {
            log.info("抽奖策略装配开始 strategyId:{}",strategyId);
            boolean armoryStatus = iStrategyArmory.assembleLotteryStrategy(strategyId);
            Response<Boolean> response = Response.<Boolean>builder()
                    .data(armoryStatus)
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .build();
            log.info("抽奖策装配完成 strategyId:{} response:{}",strategyId,armoryStatus);
            return response;
        } catch (Exception e) {
            log.info("抽奖策略装配失败 strategyId:{}",strategyId);
            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }


    @RequestMapping(value = "query_raffle_award_list",method = RequestMethod.POST)
    @Override
    public Response<List<RaffleAwardListResponseDTO>> queryRaffleAwardList(@RequestBody RaffleAwardListRequestDTO requestDTO) {
        try {
            log.info("查询抽奖奖品列表配置开始 startegyId:{}",requestDTO.getStrategyId());
            List<StrategyAwardEntity> strategyAwardEntities = iRaffleAward.queryRaffleStrategyAwardList(requestDTO.getStrategyId());
            ArrayList<RaffleAwardListResponseDTO> raffleAwardListResponseDTOS = new ArrayList<>(strategyAwardEntities.size());
            for (StrategyAwardEntity strategyAwardEntity : strategyAwardEntities) {
                raffleAwardListResponseDTOS.add(RaffleAwardListResponseDTO.builder()
                        .awardId(strategyAwardEntity.getAwardId())
                        .awardTitle(strategyAwardEntity.getAwardTitle())
                        .awardSubtitle(strategyAwardEntity.getAwardSubTitle())
                        .sort(strategyAwardEntity.getSort())
                        .build());
            }

            Response<List<RaffleAwardListResponseDTO>> response = Response.<List<RaffleAwardListResponseDTO>>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(raffleAwardListResponseDTOS)
                    .build();

            log.info("查询抽奖奖品列表配置完成 strategyId:{} response:{}",requestDTO.getStrategyId(), JSON.toJSONString(response));

            return response;
        } catch (Exception e) {
            log.error("查询抽奖奖品列表配置失败 strategyId:{}",requestDTO.getStrategyId(),e);
            return Response.<List<RaffleAwardListResponseDTO>>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }



    @RequestMapping(value = "random_raffle",method = RequestMethod.POST)
    @Override
    public Response<RaffleStrategyResponseDTO> randomRaffle(@RequestBody RaffleStrategyRequestDTO requestDTO) {
        try {
            log.info("随机抽奖开始 startegyId:{}",requestDTO.getStrategyId());
            RaffleAwardEntity raffleAwardEntity = iRaffleStrategy.performRaffle(RaffleFactorEntity.builder()
                    .userId("cxq")
                    .strategyId(requestDTO.getStrategyId())
                    .build());

            return Response.<RaffleStrategyResponseDTO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(RaffleStrategyResponseDTO.builder()
                            .awardIndex(raffleAwardEntity.getSort())
                            .awardId(raffleAwardEntity.getAwardId())
                            .build())
                    .build();
        } catch (AppException e) {
            log.error("随机抽奖失败 strategyId：{} {}", requestDTO.getStrategyId(), e.getInfo());
            return Response.<RaffleStrategyResponseDTO>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e){
            log.error("随机抽奖失败 strategyId：{}", requestDTO.getStrategyId(), e);
            return Response.<RaffleStrategyResponseDTO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }
}
