package com.micro.old.server.exception;

import com.micro.common.bean.GlobeResponse;
import com.micro.common.exception.GlobeException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class LyGameException {

    /**
     * 所有异常报错
     * 
     * @param exception
     * @return
     * @throws Exception
     */
    @ExceptionHandler(value = GlobeException.class)
    public GlobeResponse<String> myExceptionHandler(Exception exception) throws Exception {
        exception.printStackTrace();

        // 封装数据,返回给前端
        GlobeResponse<String> response = new GlobeResponse<>();
        response.setMsg(exception.getMessage());
        response.setCode("300");
        return response;
    }

    /**
     * 所有异常报错
     * 
     * @param exception
     * @return
     * @throws Exception
     */
    @ExceptionHandler(value = Exception.class)
    public GlobeResponse<String> exceptionHandler(Exception exception) throws Exception {
        exception.printStackTrace();
        // 封装数据,返回给前端
        GlobeResponse<String> response = new GlobeResponse<>();
        response.setMsg("系统繁忙,请稍后再试");
        response.setCode("520");
        response.setData(exception.getMessage());
        return response;
    }

//    /**
//     * 所有异常报错
//     *
//     * @param exception
//     * @return
//     * @throws Exception
//     */
//    @ExceptionHandler(value = UnknownSessionException.class)
//    public GlobeResponse<String> redisUnknownSessionException(Exception exception) throws Exception {
//        // 封装数据,返回给前端
//        GlobeResponse<String> response = new GlobeResponse<>();
//        response.setMsg("下线成功");
//        response.setCode("200");
//        return response;
//    }

}
