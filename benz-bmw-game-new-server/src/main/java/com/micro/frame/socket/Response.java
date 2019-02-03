package com.micro.frame.socket;

import java.util.Map;

import io.netty.channel.ChannelHandlerContext;
import lombok.Data;

/**
 * @author sam
 * @ClassName: Response
 * @Description: 客户端返回
 * @date 2018-07-25
 */
@Data
public class Response {
  public String msgType;

  public String status;
  public Map<String, Object> msg;

  public Response(int type) {
    msgType = String.valueOf(type);
  };

  public Response(int type, int state) {
    msgType = String.valueOf(type);
    status = String.valueOf(state);
  };

}
