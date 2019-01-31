package com.micro.game.server.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.micro.common.bean.GlobeResponse;

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
 * @date 2018-07-27
 */
@FeignClient(name = "account-service")
public interface AccountFeignClient {

	/**
	 * 校验token
	 * @param token
	 * @return
	 */
	@PostMapping("/acc/checkToken")
	GlobeResponse<Object> checkToken(@RequestParam("token") String token);
	
	/**
	 * 获取玩家金额
     * @param userName
     * @param siteId
     * @return GlobeResponse<Object>   
     */
	@PostMapping("/acc/getUserMoney")
	public GlobeResponse<Object> getUserMoney (@RequestParam("userName") String userName, @RequestParam("siteId") Long siteId);

}
