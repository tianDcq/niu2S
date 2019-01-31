/*
 * package com.micro.old.server.rabbitmq.handler;
 * 
 * import java.util.HashMap; import java.util.List; import java.util.Map;
 * 
 * import org.apache.commons.lang.StringUtils; import
 * org.springframework.amqp.rabbit.annotation.RabbitListener; import
 * org.springframework.beans.factory.annotation.Value; import
 * org.springframework.stereotype.Component;
 * 
 * import com.micro.common.constant.MsgTypeConstant; import
 * com.micro.common.constant.game.GameConstants; import
 * com.micro.common.util.JsonUtil; import com.micro.common.vo.RabbitMqMsg;
 * import com.micro.old.server.common.WebSocketResponse; import
 * com.micro.old.server.nettyMap.NettyChannelMap; import
 * com.micro.old.server.nettyMap.nettyData.WebSocketData; import
 * com.micro.old.server.util.CodeUtils;
 * 
 * import io.netty.channel.Channel; import
 * io.netty.handler.codec.http.websocketx.TextWebSocketFrame; import
 * lombok.extern.slf4j.Slf4j;
 * 
 *//**
	 * @ClassName: BullfightGameMQHandler
	 * @Description: 牛牛游戏消息处理器
	 * @author Henry
	 * @date 2018年8月15日
	 */
/*
 * @Component
 * 
 * @Slf4j public class BullfightGameMQHandler {
 * 
 * 
 * @Value("${webSocket.isEncrypt}") private int isEncrypt;
 *//**
	 * @Title: process
	 * @Description: 处理方法
	 * @param parseObject void
	 * @author Henry
	 * @date 2018年8月15日
	 */
/*
 * @RabbitListener(queues="#{autoDeleteQueue.name}") public void process(String
 * msg) {
 * 
 * @SuppressWarnings("unchecked") Map<String,Object> parseObject =
 * JsonUtil.parseObject(msg, Map.class);
 * 
 * Integer sendType = (Integer) parseObject.get("sendType");
 * 
 * if(sendType != null) { RabbitMqMsg one2many = JsonUtil.parseObject(msg,
 * RabbitMqMsg.class); if(sendType == MsgTypeConstant.sendType_one2many) {
 * commonSend(one2many); }
 * 
 * }else {
 * 
 * Integer msgType = (Integer) parseObject.get("msgType"); msgType = msgType ==
 * null?0:msgType; switch (msgType) { case 6001://进入游戏 enterGame(parseObject);
 * break; case 6003://上庄 bankerApply(parseObject); break; case 6005://下庄
 * bankerCancel(parseObject); break; case 6007://下注 bets(parseObject); break;
 * case 6012://退出游戏 leaveGame(parseObject); break; case
 * MsgTypeConstant.msg_type_reConnect://退出游戏 reConnect(parseObject); break;
 * default: commenHand(parseObject); } }
 * 
 * 
 * 
 * } private void commonSend(RabbitMqMsg one2many) { //String msgType =
 * one2many.getMsgType()+""; String msgType = one2many.getMsgType() == 1 ?
 * "0001" : one2many.getMsgType() + ""; Object sendMsg = one2many.getMsg();
 * //需要排除的人 WebSocketResponse<Object> webSocketResponse = new
 * WebSocketResponse<>(); webSocketResponse.setMsg(sendMsg);
 * webSocketResponse.setStatus("1"); webSocketResponse.setMsgType(msgType);
 * String msg1 = JsonUtil.parseJsonString(webSocketResponse); //加密 if(isEncrypt
 * == 1) { msg1 = CodeUtils.encode(msg1); } List<String> uniqueIds =
 * one2many.getUniqueIds(); List<WebSocketData> players =
 * NettyChannelMap.getPlayers(uniqueIds); for (WebSocketData webSocketData :
 * players) { Channel channel = webSocketData.getChannel();
 * channel.writeAndFlush(new TextWebSocketFrame(msg1)); } }
 *//**
	 * 玩家重连
	 * 
	 * @author Henry
	 * @date 2018年9月19日
	 */
/*
 * private void reConnect(Map<String, Object> parseObject) { String
 * roomNumAndToken = (String) parseObject.get("roomNum");
 * if(StringUtils.isNotBlank(roomNumAndToken)) { String[] split =
 * roomNumAndToken.split("=="); String roomNum = split[0]; String token =
 * split[1]; String GameId = GameConstants.NN.getGameId()+""; Map<String,
 * Object> stringMap = new HashMap<>(); stringMap.put("GameId", GameId);
 * stringMap.put("roomNumber", roomNum); stringMap.put("token", token);
 * List<WebSocketData> players = NettyChannelMap.getPlayers(stringMap); for
 * (WebSocketData webSocketData : players) { log.info("踢出原有连接前：" +
 * NettyChannelMap.map.size()); webSocketData.getChannel().close();
 * NettyChannelMap.removeByChannelId(webSocketData.getChannel().id()+"");
 * log.info("踢出原有连接后：" + NettyChannelMap.map.size()); } }
 * 
 * 
 * }
 *//**
	 * 离开房间
	 * 
	 * @Title: leaveGame
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param parseObject void
	 * @author Henry
	 * @date 2018年8月16日
	 */
/*
 * private void leaveGame(Map<String,Object> parseObject) { String msgType =
 * "6011"; boolean sendToSelf = false; commenSend(parseObject, msgType,
 * sendToSelf); }
 * 
 *//**
	 * 其他的后台通知消息
	 * 
	 * @Title: commenHand
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param parseObject void
	 * @author Henry
	 * @date 2018年8月16日
	 */
/*
 * private void commenHand(Map<String,Object> parseObject) {
 * commenSend(parseObject, parseObject.get("msgType")+"", true); }
 * 
 *//**
	 * 下注
	 * 
	 * @Title: bets
	 * @Description: 下注
	 * @param parseObject void
	 * @author Henry
	 * @date 2018年8月16日
	 */
/*
 * private void bets(Map<String,Object> parseObject) { String msgType = "6008";
 * boolean sendToSelf = false; commenSend(parseObject, msgType, sendToSelf);
 * 
 * }
 *//**
	 * @Title: bankerCancel
	 * @Description: 下庄
	 * @param parseObject void
	 * @author Henry
	 * @date 2018年8月16日
	 */
/*
 * private void bankerCancel(Map<String,Object> parseObject) { String msgType =
 * "6006"; boolean sendToSelf = false; commenSend(parseObject, msgType,
 * sendToSelf); }
 *//**
	 * @Description:有玩家上庄
	 * @Title: bankerApply
	 * @param parseObject void
	 * @author Henry
	 * @date 2018年8月16日
	 */
/*
 * private void bankerApply(Map<String,Object> parseObject) { String msgType =
 * "6004"; boolean sendToSelf = false; commenSend(parseObject, msgType,
 * sendToSelf); }
 *//**
	 * @Title: enterGame
	 * @Description: 有人进入游戏 将消息推送给当期房间的所有人员
	 * @param parseObject void
	 * @author Henry
	 * @date 2018年8月15日
	 */
/*
 * private void enterGame(Map<String,Object> bullfightRabbitmqMsg) { String
 * msgType = "6002"; boolean sendToSelf = false;
 * commenSend(bullfightRabbitmqMsg, msgType, sendToSelf);
 * 
 * }
 *//**
	 * 统用的消息广播
	 * 
	 * @Title: commenSend
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param bullfightRabbitmqMsg
	 * @param msgType
	 * @param sendToSelf           void
	 * @author Henry
	 * @date 2018年8月16日
	 *//*
		 * private void commenSend(Map<String,Object> bullfightRabbitmqMsg, String
		 * msgType, boolean sendToSelf) { //拿到所有的客户端,并且推送有人进入游戏的消息 String roomNum =
		 * (String) bullfightRabbitmqMsg.get("roomNum"); String gameId =
		 * GameConstants.NN.getGameId()+""; Map<String, Object> stringMap = new
		 * HashMap<>(); stringMap.put("gameId", gameId); stringMap.put("roomNumber",
		 * roomNum); List<WebSocketData> list = NettyChannelMap.getPlayers(stringMap );
		 * if(list == null || list.isEmpty()) return; WebSocketResponse<Object>
		 * webSocketResponse = new WebSocketResponse<>();
		 * webSocketResponse.setMsg(bullfightRabbitmqMsg.get("data"));
		 * webSocketResponse.setStatus("1"); webSocketResponse.setMsgType(msgType);
		 * String msg = JsonUtil.parseJsonString(webSocketResponse); //加密 if(isEncrypt
		 * == 1) { msg = CodeUtils.encode(msg); } for (WebSocketData client : list) {
		 * if(!sendToSelf) { //不给自己推送消息
		 * if(client.getUniqueId().equals(bullfightRabbitmqMsg.get("uniqueId"))) {
		 * continue; } } Channel channel = client.getChannel();
		 * 
		 * channel.writeAndFlush(new TextWebSocketFrame(msg)); } }
		 * 
		 * }
		 */