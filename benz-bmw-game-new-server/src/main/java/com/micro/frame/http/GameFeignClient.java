package com.micro.frame.http;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "GAME-SERVICE")
public interface GameFeignClient {

	@PostMapping("/game/getWildGameRoomConfigVo")
	GlobeResponse<List<RoomConfigurationVO>> getWildGameRoomConfigVo(@RequestParam("siteId") Long siteId,
			@RequestParam("gameId") Integer gameId);

	@PostMapping("/game/getWildGameRoomConfigVo2")
	public GlobeResponse<Object> getAllSiteGame(@RequestParam("gameId") Integer gameId);

}
