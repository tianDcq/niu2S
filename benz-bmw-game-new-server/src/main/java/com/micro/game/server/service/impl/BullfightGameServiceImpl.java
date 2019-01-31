package com.micro.game.server.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.micro.common.bean.GlobeResponse;
import com.micro.common.bean.account.model.TbAccAccount;
import com.micro.common.constant.game.GameConstants;
import com.micro.common.util.JsonUtil;
import com.micro.common.vo.GameRequestVO;
import com.micro.game.server.client.AccountFeignClient;
import com.micro.game.server.common.WebSocketResponse;
import com.micro.game.server.nettyMap.NettyChannelMap;
import com.micro.game.server.nettyMap.nettyData.WebSocketData;
import com.micro.game.server.service.BullfightGameService;
import com.micro.game.server.util.CodeUtils;
import com.micro.game.server.vo.UserInputVo;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

@SuppressWarnings("all")
@Service("bullfightGameService")
public class BullfightGameServiceImpl implements BullfightGameService {

	@Value("${webSocket.isEncrypt}")
	private Integer isEncrypt;
	
	
	@Autowired
	private AccountFeignClient accountFeignClient;
	
	@Override
	public GlobeResponse commenHand(GameRequestVO webSocketRequest, ChannelHandlerContext ctx) {
		 
		return null;
	}
	
	public GlobeResponse refresh(GameRequestVO webSocketRequest, ChannelHandlerContext ctx) {
		WebSocketData webSocketData = NettyChannelMap.get(ctx.channel().id()+"");
		webSocketRequest.setRoomNum(webSocketData.getRoomNumber());
		return null;
	}
	
	public void connectWebsocket(String msgType, String token, String userMsg, ChannelHandlerContext ctx) {
		Map<String, String> map = new HashMap();
		map.put("token", token);
		map.put("msgType", "6001");
		GameRequestVO webSocketRequest = (GameRequestVO) JsonUtil
				.parseObject(JsonUtil.parseJsonString(map), GameRequestVO.class);
		webSocketRequest.setUserMsg(userMsg);
		GlobeResponse globeResponse = this.enterGame(webSocketRequest, ctx);
		WebSocketResponse webSocketResponse = new WebSocketResponse();
		webSocketResponse.setMsgType(webSocketRequest.getMsgType()+"");
		webSocketResponse.setStatus("1");
		if (!globeResponse.getCode().equals("200")) {
			webSocketResponse.setStatus("0");
			webSocketResponse.setMsg(globeResponse.getMsg());
		} else {
			webSocketResponse.setMsg(globeResponse.getData());
		}

		String msg = JsonUtil.parseJsonString(webSocketResponse);
		if (this.isEncrypt == 1) {
			msg = CodeUtils.encode(msg);
		}

		ctx.writeAndFlush(new TextWebSocketFrame(msg));
	}
	
	/**
	 * 进入游戏
	 */
	@Override
	public GlobeResponse enterGame(GameRequestVO enterBullfight, ChannelHandlerContext ctx) {
		String userMsg = enterBullfight.getUserMsg();
		UserInputVo inputVo = JsonUtil.parseObject(userMsg, UserInputVo.class);
		Long siteId = inputVo.getSiteId();
		String userName = inputVo.getAccount();	
		
		GlobeResponse<Object> globeResponse;
		
		if(inputVo.getIdentity().equals(1)) {						
			globeResponse = accountFeignClient.getUserMoney(userName, siteId);
			
			if (!globeResponse.getCode().equals("200")) {
				GlobeResponse gr = new GlobeResponse();
				gr.setGlobeResponse(globeResponse.getCode(), globeResponse.getMsg());
				return gr;
			}
			
			TbAccAccount account = JsonUtil.parseObject(JsonUtil.parseJsonString(globeResponse.getData()), TbAccAccount.class);
			inputVo.setMoney(account.getMoney());
			enterBullfight.setUserMsg(JsonUtil.parseJsonString(inputVo));
		}				
		
		globeResponse = null;//bullfightGameFeignClient.enterGame(enterBullfight);
		//将当前客户端与游戏房间绑定
		if(globeResponse.getCode().equals("200")) {
			String roomNum = ((Map)globeResponse.getData()).get("roomNum").toString();			
			Object object = ((Map)globeResponse.getData()).get("clearToken");
			if(object != null) {
				//将之前的socket连接删除
				String clearBefore  = ((Map)globeResponse.getData()).get("clearToken").toString();
				Map<String, Object> stringMap = new HashMap<>();
				stringMap.put("token", clearBefore);
				 
			}
			
			 
			WebSocketData webSocketData = NettyChannelMap.get(ctx.channel().id()+"");
			webSocketData.setAccount(inputVo.getAccount());
			webSocketData.setGameId(GameConstants.NN.getGameId()+"");
			webSocketData.setRoomNumber(roomNum);
			webSocketData.setSiteId(String.valueOf(siteId));
			webSocketData.setUniqueId(enterBullfight.getUniqueId());
			webSocketData.setToken(enterBullfight.getToken());
			NettyChannelMap.putInMap(enterBullfight.getUniqueId(),webSocketData);
		}
		return globeResponse;
	}

	/**
	 * 玩家申请上庄
	 */
	@Override
	public GlobeResponse bankerApply(GameRequestVO enterBullfight,ChannelHandlerContext ctx) {
		
		return null;
	}
	/**
	 * @Title: bankerCancel  
	 * @Description: 申请下庄  
	 * @param webSocketRequest
	 * @param ctx
	 * @return GlobeResponse  
	 * @author Henry  
	 * @date 2018年8月15日
	 */
	@Override
	public GlobeResponse bankerCancel(GameRequestVO webSocketRequest, ChannelHandlerContext ctx) {
		return null;
	}
	/**
	 * @Title: bets  
	 * @Description: 下注 
	 * @param webSocketRequest
	 * @param ctx
	 * @return GlobeResponse  
	 * @author Henry  
	 * @date 2018年8月15日
	 */
	@Override
	public GlobeResponse bets(GameRequestVO webSocketRequest, ChannelHandlerContext ctx) {
		return null;
	}
	/**
	 * @Title: leaveGame  
	 * @Description: 离开游戏  
	 * @param webSocketRequest
	 * @param ctx
	 * @return GlobeResponse  
	 * @author Henry  
	 * @date 2018年8月15日
	 */
	@Override
	public GlobeResponse leaveGame(GameRequestVO webSocketRequest, Channel channel) {
		return null;
	}
	/**
	 * @Title: historyRecord  
	 * @Description: 获取游戏开奖历史纪录  
	 * @param webSocketRequest
	 * @param ctx
	 * @return GlobeResponse  
	 * @author Henry  
	 * @date 2018年8月15日
	 */
	@Override
	public GlobeResponse historyRecord(GameRequestVO webSocketRequest, ChannelHandlerContext ctx) {
		
		return null;
	}
	/**
	 * 用户掉线
	 */
	@Override
	public void webSocketBroke(ChannelHandlerContext ctx) {
		
		WebSocketData webSocketData = NettyChannelMap.get(ctx.channel().id()+"");
		String roomNum = webSocketData.getRoomNumber();
		String siteId = webSocketData.getSiteId();
		String account = webSocketData.getAccount();
		String token = webSocketData.getToken();
		String uniqueId = webSocketData.getUniqueId();
		NettyChannelMap.removeByChannelId(webSocketData.getChannel().id()+"");
	}
	/**
	 * 获取房间配置
	 */
	@Override
	public GlobeResponse getRoomConfig(GameRequestVO webSocketRequest, ChannelHandlerContext ctx) {
		WebSocketData webSocketData = NettyChannelMap.get(ctx.channel().id()+"");
		webSocketData.setUniqueId(webSocketRequest.getUniqueId());
		NettyChannelMap.putInMap(webSocketRequest.getUniqueId(),webSocketData);
		return null;
	}

	@Override
	public GlobeResponse playerGameResult(GameRequestVO webSocketRequest, ChannelHandlerContext ctx) {		
		return null;
	}

	@Override
	public GlobeResponse getRoomList(GameRequestVO webSocketRequest, ChannelHandlerContext ctx) {
		return null;
	}
}
