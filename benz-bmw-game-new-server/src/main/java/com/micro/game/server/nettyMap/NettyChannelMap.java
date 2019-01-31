package com.micro.game.server.nettyMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;

import com.micro.game.server.nettyMap.nettyData.WebSocketData;

import io.netty.channel.ChannelId;

/**
 * @author sam
 * @ClassName: NettyChannelMap
 * @Description: 管理连接
 * @date 2018-08-04
 */
public class NettyChannelMap {

	/**
	 * key : channelId
	 */
	public static Map<ChannelId, WebSocketData> map = new ConcurrentHashMap<>();
	/**
	 * key : uniqueId
	 */
	public static Map<String, WebSocketData> map1 = new ConcurrentHashMap<String, WebSocketData>();

	// 添加 channel
	public static void add(ChannelId channelId, WebSocketData webSocketData) {
		map.put(channelId, webSocketData);
	}
	public static void putInMap(String uniqueId, WebSocketData webSocketData) {
		map1.put(uniqueId, webSocketData);
	}
	
	// 获取 channel
	public static WebSocketData get(String channelId) {
		return map.get(channelId);
	}

	// 删除 channel
	public static void removeByChannelId(String channelId) {
		WebSocketData webSocketData = map.get(channelId);
		map1.remove(webSocketData.getUniqueId());
		map.remove(channelId);
	}

	/**
	 * 查询channel
	 * 
	 * @param stringMap
	 * @return
	 */
	public static List<WebSocketData> getPlayers(Collection<String> uniqueIds) {
		List<WebSocketData> list = new ArrayList<>();
		if (map1.isEmpty() || uniqueIds.isEmpty()) {
			return list;
		}
		for (String uniqueId : uniqueIds) {
			WebSocketData webSocketData = map1.get(uniqueId);
			if(webSocketData != null) {
				list.add(webSocketData);
			}
		}
		return list;
	}
	/**
	 * 查询channel
	 * 
	 * @param stringMap
	 * @return
	 */
	public static List<WebSocketData> getPlayers(Map<String, Object> stringMap) {
		List<WebSocketData> list = new ArrayList<>();
		if (map.isEmpty() || stringMap == null) {
			return list;
		}
		return list;
	}

	/**
	 * 更新websocket对象
	 * 
	 * @param channelId
	 * @param stringMap
	 */
	public static void updateByChannelId(String channelId, Map<String, Object> stringMap) {
		if (map.isEmpty() || stringMap == null) {
			return;
		}
		 
	}

}
