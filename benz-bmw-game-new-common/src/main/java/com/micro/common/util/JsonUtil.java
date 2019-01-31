package com.micro.common.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @Description:JSON工具类
 * @auth:Anthony
 * @Date:2018/8/10 16:58
 * @return:
 */
public class JsonUtil {

    public static ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 	对 各种对象进行 序列化和反序列号的  gson 对象。 具体使用样例 参见  项目   slot-game-service  com.micro.threecard.util.GsonUtil
     */
    private static final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
	
    
    static {
        //序列化的时候序列对象的所有属性
        objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);

        //反序列化的时候如果多了其他属性,不抛出异常
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        //如果是空对象的时候,不抛异常
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        //取消时间的转化格式,默认是时间戳,可以取消,同时需要设置要表现的时间格式
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));


    }

    /**
     * @Description: 对象转JSON
     * @params: 实体类
     * @auth:Anthony
     * @Date:2018/8/10 16:57
     * @return:
     */
    public static String parseJsonString(Object o){
        if (o==null){
            return null;
        }
        try {
            return objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @Description:JSON转 T对象
     * @auth:Anthony
     * @Date:2018/8/10 16:57
     * @return:
     */
    public static <T> T parseObject(String json,Class<T> cla) {
        if (json==null||json.equals(""))
            return null;
        try {
            return objectMapper.readValue(json, cla);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }
    /**
     * @Description:JSON转 T对象
     * @auth:Anthony
     * @Date:2018/8/10 16:57
     * @return:
     */
    public static <T> T parseObject(Object o,Class<T> cla) {
    	 return parseObject(parseJsonString(o),cla);
    }

    /**
     * @Description:JSON转 List对象,第二个参数,定义泛型
     * @auth:Anthony
     * @Date:2018/8/10 16:57
     * @return:
     */
    public static <T> List<T> parseObjectList(String json,Class<T> cla) {
        if (json==null||json.equals(""))
            return null;
        try {
            JavaType javaType = JsonUtil.objectMapper.getTypeFactory().constructParametricType(List.class, cla);
            List<T> list = JsonUtil.objectMapper.readValue(json, javaType);
            return list;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
	 * @Title: object2String  
	 * @Description:  使用gson 把一个对象转换为json字符串，支持gson的各种注解。（ 根据具有隐藏object中的某些字段，隐藏的字段需要使用注解进行标记）  
	 * @param o
	 * @return String
	 * @author seeanknow  
	 * @date 2018年8月28日
	 */
	 
	/**
	 * 
	 * @Title: object2String  
	 * @Description: 把json字符串转换为 类对象。第一个参数是 json、字符串 ；第二个类型为 Class 类型。
	 * @param json
	 * @param cla
	 * @return T  
	 * @author seeanknow  
	 * @date 2018年8月28日
	 */
	 
	
	
	/**
	 * @Title: json2ObjectList  
	 * @Description:  把json字符串转为list。 如果json 为空，则 返回null
	 * @param json
	 * @param cla
	 * @return List<T>  
	 * @author seeanknow  
	 * @date 2018年8月28日
	 */
 
	
	/**
	 * 
	 * @Title: json2Map  
	 * @Description: json字符串 转换为map类型的数据
	 * @param json
	 * @return Map  
	 * @author seeanknow  
	 * @date 2018年8月28日
	 */
 
	
	
    public static void main(String[] args) throws IOException {


    }

	
}
