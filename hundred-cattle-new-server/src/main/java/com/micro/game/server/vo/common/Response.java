package com.micro.game.server.vo.common;

import lombok.Data;

/**
 * @author sam
 * @ClassName: Response
 * @Description: 客户端返回
 * @date 2018-07-25
 */
@Data
public class Response<T> {

  private String msgType;

  private String status;

  private T msg;

}
