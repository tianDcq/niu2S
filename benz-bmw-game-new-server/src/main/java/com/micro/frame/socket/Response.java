package com.micro.frame.socket;

import java.util.Map;

import lombok.Data;

/**
 * @author sam
 * @ClassName: Response
 * @Description: 客户端返回
 * @date 2018-07-25
 */
public class Response extends BaseRespone{
  public String msgType;

  public String status;
  public Map<String, Object> msg;

  public Response(int type) {
    super(type);
  };

  public Response(int type, int state) {
    super(type,state);
  };

}
