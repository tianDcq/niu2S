package com.micro.game.server.common;

import java.io.Serializable;
import lombok.Data;

/**
 * @author sam
 * @ClassName: WebSocketResponse
 * @Description: 返回客户端消息
 * @date 2018-07-27
 */
@Data
public class WebSocketResponse<T> implements Serializable {

  private static final long serialVersionUID = -1;

  private String status;

  private String msgType;

  private T msg;

}
