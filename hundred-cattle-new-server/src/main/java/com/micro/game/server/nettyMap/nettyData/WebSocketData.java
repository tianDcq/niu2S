package com.micro.game.server.nettyMap.nettyData;

import io.netty.channel.Channel;
import lombok.Data;

/**
 * @author sam
 * @ClassName: WebSocketData
 * @Description: 连接对象
 * @date 2018-08-04
 */
@Data
public class WebSocketData {
  private Channel channel;
  private String gameId;//游戏标识
  private String siteId;//厅主id
  private String account;//账户名
  private String roomNumber;//房间号
  private String tableNumber;//桌号
  private String token;
  private String uniqueId;//用户唯一标识
}
