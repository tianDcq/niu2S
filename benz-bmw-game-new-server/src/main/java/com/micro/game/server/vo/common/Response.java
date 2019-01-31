package com.micro.game.server.vo.common;

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

  public Map<String, Object> msg;
}