package com.micro.frame.http;

import com.micro.common.bean.GlobeResponse;
import com.micro.common.bean.account.model.TbGameEachRecord;
import com.micro.common.vo.GameResultInputVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@FeignClient(name = "GAME-SERVICE")
public interface GameFeignClient {

	@PostMapping("/game/getWildGameRoomConfigVo")
    GlobeResponse<List<RoomConfigurationVO>> getWildGameRoomConfigVo(@RequestParam("siteId") Long siteId, @RequestParam("gameId") Integer gameId);

	@PostMapping("/game/getWildGameRoomConfigVo2")
	public GlobeResponse<Object> getAllSiteGame(@RequestParam("gameId") Integer gameId);


}
