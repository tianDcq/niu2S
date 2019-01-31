package com.micro.old.server.config;

import com.alibaba.fastjson.JSON;
import com.micro.old.server.util.CodeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * @author sam
 * @ClassName: DecryptRequestBodyAdvice
 * @Description: json请求数据加密
 * @date 2018-08-01
 */
public class EncryptResponseBodyAdvice implements ResponseBodyAdvice<Object> {

	/**
	 * 是否加密
	 */
	@Value("${http.isEncrypt}")
	private Integer isEncrypt;

	private Logger logger = LoggerFactory.getLogger(EncryptResponseBodyAdvice.class);

	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		return true;
	}

	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
			Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
			ServerHttpResponse response) {
		try {
			// 加密
			if (isEncrypt == 1) {
				String content = JSON.toJSONString(body);
				content = CodeUtils.encode(content);
				return content;
			}
		} catch (Exception e) {
			logger.error("加密数据异常", e);
		}
		return body;
	}

}
