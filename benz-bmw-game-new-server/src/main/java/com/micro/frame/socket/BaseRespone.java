package com.micro.frame.socket;

import lombok.Data;

/**
 * @author sam
 * @ClassName: Response
 * @Description: 客户端返回
 * @date 2018-07-25
 */
@Data
public class BaseRespone {
  public String msgType;
  public String status;
  public BaseRespone(int type) {
    msgType = String.valueOf(type);
  };

  public BaseRespone(int type, int state) {
    msgType = String.valueOf(type);
    status = String.valueOf(state);
  };

}