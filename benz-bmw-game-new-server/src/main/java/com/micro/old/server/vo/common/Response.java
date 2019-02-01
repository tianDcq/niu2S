package com.micro.old.server.vo.common;

import java.util.Map;

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

  public Response(String type) {
    msgType = type;
  };

  public Response(String type, String state) {
    msgType = type;
    status = state;
  };

  public Map<String, Object> msg;
}
