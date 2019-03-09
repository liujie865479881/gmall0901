package com.atguigu.gmall0901.order.mq;

import com.atguigu.gmall0901.bean.enums.ProcessStatus;
import com.atguigu.gmall0901.service.OrderService;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;

@Component
public class OrderConsumer {

    @Autowired
    private OrderService orderService;


    //获取消息队列中的消息  监听PAYMENT_RESULT_QUEUE当中的消息
    @JmsListener(destination = "PAYMENT_RESULT_QUEUE",containerFactory = "jmsQueueListener")
    public void consumerPaymentResult(ActiveMQMapMessage activeMQMapMessage) throws JMSException {
        //获取消息队列中的数据
        String orderId = activeMQMapMessage.getString("orderId");
        String result = activeMQMapMessage.getString("result");

        System.out.println("orderId:=" + orderId + "\t result:=" + result);

        if("success".equals(result)){
            //修改订单状态
            orderService.updateOrderStatus(orderId, ProcessStatus.PAID);

            //发送通知给库存
            orderService.sendOrderStatus(orderId);

            //发送完通知之后  更改订单状态
            orderService.updateOrderStatus(orderId, ProcessStatus.NOTIFIED_WARE);
        }else {
            orderService.updateOrderStatus(orderId, ProcessStatus.UNPAID);
        }
    }


    @JmsListener(destination = "SKU_DEDUCT_QUEUE",containerFactory = "jmsQueueListener")
    public void consumeSkuDeduct(ActiveMQMapMessage activeMQMapMessage) throws JMSException {
        //获取消息队列中的数据
        String orderId = activeMQMapMessage.getString("orderId");
        String status = activeMQMapMessage.getString("status");


        if("DEDUCTED".equals(status)){
            //修改订单状态
            orderService.updateOrderStatus(orderId, ProcessStatus.DELEVERED);

        }else {
            orderService.updateOrderStatus(orderId, ProcessStatus.STOCK_EXCEPTION);
        }
    }

}
