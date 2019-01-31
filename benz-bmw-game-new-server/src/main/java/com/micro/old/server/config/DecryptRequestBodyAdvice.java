package com.micro.old.server.config;

import com.micro.old.server.util.CodeUtils;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

/**
 * @author sam
 * @ClassName: DecryptRequestBodyAdvice
 * @Description: json请求数据解密
 * @date 2018-08-01
 */
// @ControllerAdvice(basePackages = "com.micro.old.server.controller")
public class DecryptRequestBodyAdvice implements RequestBodyAdvice {

  /**
   * 是否加密
   */
  @Value("${http.isEncrypt}")
  private Integer isEncrypt;

  @Override
  public boolean supports(MethodParameter methodParameter, Type targetType,
      Class<? extends HttpMessageConverter<?>> converterType) {
    return true;
  }

  @Override
  public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
      Class<? extends HttpMessageConverter<?>> converterType) {
    return body;
  }

  @Override
  public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
      Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
    try {
      return new MyHttpInputMessage(inputMessage);
    } catch (Exception e) {
      e.printStackTrace();
      return inputMessage;
    }
  }

  @Override
  public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
      Class<? extends HttpMessageConverter<?>> converterType) {
    return body;
  }

  class MyHttpInputMessage implements HttpInputMessage {
    private HttpHeaders headers;

    private InputStream body;

    public MyHttpInputMessage(HttpInputMessage inputMessage) throws Exception {
      this.headers = inputMessage.getHeaders();
      String encryptionStr = IOUtils.toString(inputMessage.getBody(), "UTF-8");
      // 解密
      if (isEncrypt == 1) {
        encryptionStr = CodeUtils.decode(encryptionStr);
      }
      this.body = IOUtils.toInputStream(encryptionStr, "UTF-8");
    }

    @Override
    public InputStream getBody() throws IOException {
      return body;
    }

    @Override
    public HttpHeaders getHeaders() {
      return headers;
    }
  }
}
