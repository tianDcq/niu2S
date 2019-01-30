package com.micro.game.server.common;

import lombok.Data;

/**
 * @author sam
 * @ClassName: WebSocketRequest
 * @Description: 接收客户端消息
 * @date 2018-07-27
 */
@Data
public class WebSocketRequest<T> {

  private String msgType;
  private String token;

}
