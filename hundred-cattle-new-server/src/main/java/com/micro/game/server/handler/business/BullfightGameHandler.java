package com.micro.game.server.handler.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.micro.common.bean.GlobeResponse;
import com.micro.common.util.JsonUtil;
import com.micro.common.vo.GameRequestVO;
import com.micro.game.server.common.WebSocketResponse;
import com.micro.game.server.service.BullfightGameService;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author henry
 * @ClassName: BullfightGameHandler
 * @Description: 牛牛游戏handler
 * @date 2018-08-14
 */
@SuppressWarnings("all")
@Service
@Slf4j
public class BullfightGameHandler extends AbstractGameHandler {

	public void setUserMsg(String userMsg) {
		this.userMsg = userMsg;
	}
	
	@Autowired
	private BullfightGameService bullfightGameService;

	@Override
	public void execute(String request, WebSocketResponse webSocketResponse, ChannelHandlerContext ctx) throws Exception {
		GameRequestVO webSocketRequest = JsonUtil.parseObject(request, GameRequestVO.class);
		try {
			webSocketRequest.setUserMsg(this.userMsg);
			int msgType = Integer.valueOf(webSocketRequest.getMsgType());
			GlobeResponse globeResponse = null;
			switch (msgType) {
			case 6001:// 进入游戏
				globeResponse = bullfightGameService.enterGame(webSocketRequest, ctx);
				break;
			case 6003:// 申请上庄
				globeResponse = bullfightGameService.bankerApply(webSocketRequest, ctx);
				break;
			case 6005:// 申请下庄
				globeResponse = bullfightGameService.bankerCancel(webSocketRequest, ctx);
				break;
			case 6007:// 下注
				globeResponse = bullfightGameService.bets(webSocketRequest, ctx);
				break;
			case 6012:// 离开游戏
				globeResponse = bullfightGameService.leaveGame(webSocketRequest, ctx.channel());
				break;
			case 6013:// 获取游戏开奖历史纪录
				globeResponse = bullfightGameService.historyRecord(webSocketRequest, ctx);
				break;
			case 6014://获取房间配置
				globeResponse = bullfightGameService.getRoomConfig(webSocketRequest, ctx);
				break;
			case 6016 ://刷新
				globeResponse = this.bullfightGameService.refresh(webSocketRequest, ctx);
				break;
			case 6018: //获取房间列表
				globeResponse = bullfightGameService.getRoomList(webSocketRequest, ctx);
				break;
			case 6019: //获取玩家牌局记录
				globeResponse = bullfightGameService.playerGameResult(webSocketRequest, ctx);
				break;				
			default://通用处理
				globeResponse = this.bullfightGameService.commenHand(webSocketRequest, ctx);
			}
			webSocketResponse.setMsgType(webSocketRequest.getMsgType()+"");
			webSocketResponse.setStatus("1");
			if (!globeResponse.getCode().equals("200")) {
				webSocketResponse.setStatus("0");
				webSocketResponse.setMsg(globeResponse.getMsg());
			} else {
				webSocketResponse.setMsg(globeResponse.getData());
			}
		} catch (Exception e) {
			 e.printStackTrace();
			 log.error("百人牛牛消息处理异常：",e);
		}
		
	}

}
