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

/**
 * 
 * @ClassName: AccountFeignClient  
 * @Description: 
 * * feign与@RequestParam配合使用时，一定要写value值。
 * feign方法的@RequestMapping，务必与服务端方法保持一致，请求类型，请求参数，返回值等等
 * 
 * TODO 可以从服务端定义一个接口层，服务实现层实现接口，调用方扩展此接口，即可完成接口定义的复用，而无须在此重新复制一次。
 * 但此举会导致服务端接口变动后，调用方就会直接受影响，建议事先约定好规则
 * @author sam
 * @date 2018-08-02
 */
@FeignClient(name = "GAME-SERVICE")
public interface GameFeignClient {

	@PostMapping("/game/getWildGameRoomConfigVo")
    GlobeResponse<List<RoomConfigurationVO>> getWildGameRoomConfigVo(@RequestParam("siteId") Long siteId, @RequestParam("gameId") Integer gameId);

	@PostMapping("/game/updateAccountMoney")
    GlobeResponse<Object> updateAccountMoney(@RequestBody List<GameResultInputVO> gameResultInputVOList);

	@PostMapping("/game/insertGameEachRecord")
    GlobeResponse<Object> insertGameEachRecord(@RequestBody TbGameEachRecord gameRecord);
	/**
	 * 游戏是否维护中
	 * @param siteId
	 * @param gameId
	 * @return
	 */
	@PostMapping("/game/isGameMaintain")
    GlobeResponse<Object> isGameMaintain(@RequestParam("siteId") Long siteId, @RequestParam("gameId") Integer gameId);

	/**
	 * 获取随机姓名
	 * @return
	 */
	@PostMapping("/game/getRandomName")
    GlobeResponse<String> getRandomName();

	/**
	 * 修改对应游戏房间真实库存
	 * 传 roomId + inventory 与 siteId + gameId + inventory
	 * @param siteId
	 * @param gameId
	 * @param inventory
	 * @param roomId
	 * @return
	 */
	@PostMapping("/game/updateGameStock")
    GlobeResponse<String> updateGameStock(@RequestParam("siteId") Long siteId, @RequestParam("gameId") Integer gameId,
                                          @RequestParam("inventory") BigDecimal inventory, @RequestParam("roomId") Integer roomId);

//	/**
//	 * 获取开奖结果
//	 * @param gameOpenPrizeVO 玩家下注信息
//	 * @return
//	 */
//	@PostMapping("/gameAccount/getOpenPrize")
//    GlobeResponse<Object> getOpenPrize(@RequestBody GameOpenPrizeVO gameOpenPrizeVO);
//
//	@PostMapping("/gameAccount/getSiteGameInventory")
//    GlobeResponse<Object> getSiteGameInventory(@RequestBody TbSiteGameInventoryVo tbSiteGameInventoryVo);
}
