package com.micro.game.server.service;

import com.micro.common.bean.GlobeResponse;
import com.micro.common.vo.GameRequestVO;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

/**
 * 牛牛游戏服务
 * @author Henry
 *
 */
public interface BullfightGameService {
	/**
	 * 进入游戏
	 * @param msgType
	 * @param token
	 * @param string 
	 * @param integer 
	 * @param ctx
	 * @return
	 */
	GlobeResponse enterGame(GameRequestVO webSocketRequest, ChannelHandlerContext ctx);
	/**
	 * @Title: bankerApply  
	 * @Description: 上庄申请  
	 * @param msgType
	 * @param token
	 * @param roomType
	 * @param stationMark
	 * @param ctx
	 * @return GlobeResponse  
	 * @author Henry  
	 * @date 2018年8月15日
	 */
	GlobeResponse bankerApply(GameRequestVO webSocketRequest, ChannelHandlerContext ctx);
	/**
	 * @Title: bankerCancel  
	 * @Description: 申请下庄  
	 * @param webSocketRequest
	 * @param ctx
	 * @return GlobeResponse  
	 * @author Henry  
	 * @date 2018年8月15日
	 */
	GlobeResponse bankerCancel(GameRequestVO webSocketRequest, ChannelHandlerContext ctx);
	/**
	 * @Title: bets  
	 * @Description: 下注 
	 * @param webSocketRequest
	 * @param ctx
	 * @return GlobeResponse  
	 * @author Henry  
	 * @date 2018年8月15日
	 */
	GlobeResponse bets(GameRequestVO webSocketRequest, ChannelHandlerContext ctx);
	/**
	 * @Title: leaveGame  
	 * @Description: 离开游戏  
	 * @param webSocketRequest
	 * @param ctx
	 * @return GlobeResponse  
	 * @author Henry  
	 * @date 2018年8月15日
	 */
	GlobeResponse leaveGame(GameRequestVO webSocketRequest, Channel channel);
	/**
	 * @Title: historyRecord  
	 * @Description: 获取游戏开奖历史纪录  
	 * @param webSocketRequest
	 * @param ctx
	 * @return GlobeResponse  
	 * @author Henry  
	 * @date 2018年8月15日
	 */
	GlobeResponse historyRecord(GameRequestVO webSocketRequest, ChannelHandlerContext ctx);
	/**
	 * 用户掉线
	 * @Title: webSocketBroke  
	 * @Description: TODO(这里用一句话描述这个方法的作用)  
	 * @param ctx void  
	 * @author Henry  
	 * @date 2018年8月21日
	 */
	void webSocketBroke(ChannelHandlerContext ctx);
	/**
	 * 获取房间配置
	 * @Title: getRoomConfig  
	 * @Description: TODO(这里用一句话描述这个方法的作用)  
	 * @param webSocketRequest
	 * @param ctx
	 * @return GlobeResponse  
	 * @author Henry  
	 * @date 2018年8月22日
	 */
	GlobeResponse getRoomConfig(GameRequestVO webSocketRequest, ChannelHandlerContext ctx);
	/**
	 * 刷新
	 * @Title: refresh  
	 * @Description: TODO(这里用一句话描述这个方法的作用)  
	 * @param webSocketRequest
	 * @param ctx
	 * @return GlobeResponse  
	 * @author Henry  
	 * @date 2018年9月10日
	 */
	GlobeResponse refresh(GameRequestVO webSocketRequest, ChannelHandlerContext ctx); 
	/**
	 * 通用处理
	 * @author Henry  
	 * @date 2018年9月12日
	 */
	GlobeResponse commenHand(GameRequestVO webSocketRequest, ChannelHandlerContext ctx);
	
	/**
	 * 获取玩家历史对局记录
	 * @Title: commenHand  
	 * @Description: 
	 * @param webSocketRequest
	 * @param ctx
	 * @return GlobeResponse  
	 * @author Logan 
	 * @date 2018年10月22日
	 */
	GlobeResponse playerGameResult(GameRequestVO webSocketRequest, ChannelHandlerContext ctx);
	
	/**
	 * 
	 * @Title: getRoomList  
	 * @Description: 获取房间列表
	 * @param webSocketRequest
	 * @param ctx
	 * @return GlobeResponse  
	 * @author Logan 
	 * @date 2018年10月23日
	 */
	GlobeResponse getRoomList(GameRequestVO webSocketRequest, ChannelHandlerContext ctx);
  
}
