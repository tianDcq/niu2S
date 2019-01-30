/*package com.micro.game.server.rabbitmq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.micro.common.constant.MQMsgConstant;
import com.micro.common.util.ServerIpconfig;

*//**
 * 
 * @ClassName: RabbitConfig  
 * @Description: rabbitmq的配置类。
 * ** 考虑到消息队列过多， 需要考虑取消消息队列。 维护消息队列的方法参考：
 * *，https://www.cnblogs.com/duanxz/p/7493276.html
 * @author: seeanknow  
 * @date: 2018年9月11日
 *//*

@Configuration
public class RabbitConfig {

    *//**
     * 创建人：张博
     * 时间：2018/3/5 上午10:45
     * @apiNote 定义扇出（广播）交换器
     *//*
    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(MQMsgConstant.exchangeName);
    }

 
    *//**
     * 创建人：张博
     * 时间：2018/3/5 上午10:48
     * @apiNote 定义自动删除匿名队列
     *//*
    @Bean
    public Queue autoDeleteQueue() {
        return new Queue("hundred-cattle-server-queue" + ServerIpconfig.getHostIp(), true, false, true, null);
    }

    *//**
     * 创建人：张博
     * 时间：2018/3/5 上午10:55
     * @param fanoutExchange 扇出（广播）交换器
     * @param autoDeleteQueue1 自动删除队列
     * @apiNote 把队列绑定到扇出（广播）交换器
     * @return Binding
     *//*
    @Bean
    public Binding binding(FanoutExchange fanoutExchange, Queue autoDeleteQueue) {
        return BindingBuilder.bind(autoDeleteQueue).to(fanoutExchange);
    }
}
    */