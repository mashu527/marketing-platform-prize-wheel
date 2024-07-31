package org.cxq.trigger.api;

import org.cxq.trigger.api.dto.RaffleAwardListRequestDTO;
import org.cxq.trigger.api.dto.RaffleAwardListResponseDTO;
import org.cxq.trigger.api.dto.RaffleRequestDTO;
import org.cxq.trigger.api.dto.RaffleResponseDTO;
import org.cxq.types.model.Response;

import java.util.List;

/**
 * 抽奖服务接口
 */
public interface IRaffleService {
    /**
     * 策略装配接口
     * @param strategyId 策略ID
     * @return 装配结果
     */
    Response<Boolean> StrategyArmory(Long strategyId);

    /**
     * 抽奖奖品列表配置
     * @param requestDTO 抽奖奖品列表请求参数
     * @return 奖品列表数据
     */
    Response<List<RaffleAwardListResponseDTO>> queryRaffleAwardList(RaffleAwardListRequestDTO requestDTO);

    /**
     * 随机抽奖接口
     * @param requestDTO 请求参数
     * @return 抽奖结果
     */
    Response<RaffleResponseDTO> randomRaffle(RaffleRequestDTO requestDTO);
}
