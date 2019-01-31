package com.micro.old.server.handler.business;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.micro.common.bean.GlobeResponse;
import com.micro.common.util.JsonUtil;
import com.micro.common.vo.GameRequestVO;
import com.micro.old.server.client.AccountFeignClient;
import com.micro.old.server.common.WebSocketResponse;
import com.micro.old.server.service.BullfightGameService;

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
public class BullfightGameHandler {

	@Resource
	private AccountFeignClient accountFeignClient;

	@Autowired
	private BullfightGameService bullfightGameService;

	public void execute(String request, WebSocketResponse webSocketResponse, ChannelHandlerContext ctx)
			throws Exception {
		GameRequestVO webSocketRequest = JsonUtil.parseObject(request, GameRequestVO.class);
		try {
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
			case 6014:// 获取房间配置
				globeResponse = bullfightGameService.getRoomConfig(webSocketRequest, ctx);
				break;
			case 6016:// 刷新
				globeResponse = this.bullfightGameService.refresh(webSocketRequest, ctx);
				break;
			case 6018: // 获取房间列表
				globeResponse = bullfightGameService.getRoomList(webSocketRequest, ctx);
				break;
			case 6019: // 获取玩家牌局记录
				globeResponse = bullfightGameService.playerGameResult(webSocketRequest, ctx);
				break;
			default:// 通用处理
				globeResponse = this.bullfightGameService.commenHand(webSocketRequest, ctx);
			}
			webSocketResponse.setMsgType(webSocketRequest.getMsgType() + "");
			webSocketResponse.setStatus("1");
			if (!globeResponse.getCode().equals("200")) {
				webSocketResponse.setStatus("0");
				webSocketResponse.setMsg(globeResponse.getMsg());
			} else {
				webSocketResponse.setMsg(globeResponse.getData());
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("百人牛牛消息处理异常：", e);
		}

	}

	/**
	 * 处理长连接请求
	 * 
	 * @param request 客户端发来的请求数据
	 * @param ctx     长连接对象
	 * @author Henry
	 * @date 2019年1月30日
	 */
	public WebSocketResponse processRequest(String request, ChannelHandlerContext ctx) {
		WebSocketResponse webSocketResponse = new WebSocketResponse();
		Map maps = null;
		try {
			maps = (Map) JSON.parse(request);
			String token = String.valueOf(maps.get("token"));
			String msgTypeStr = String.valueOf(maps.get("msgType"));
			/*
			 * GlobeResponse<Object> globeResponse = accountFeignClient.checkToken(token);
			 * if (!globeResponse.getCode().equals("200")){
			 * webSocketResponse.setStatus("-99"); webSocketResponse.setMsgType(msgTypeStr);
			 * webSocketResponse.setMsg("token校验失败"); return webSocketResponse; } String
			 * userMsg = String.valueOf(globeResponse.getData());
			 */
			execute(request, webSocketResponse, ctx);
		} catch (Exception ex) {
			webSocketResponse.setStatus("0");
			if (maps != null) {
				webSocketResponse.setMsgType(String.valueOf(maps.get("msgType")));
			}
			if (ex.getMessage() == null) {
				webSocketResponse.setMsg("消息处理异常");
			} else {
				webSocketResponse.setMsg(ex.getMessage());
			}
		}
		return webSocketResponse;

	}

}
