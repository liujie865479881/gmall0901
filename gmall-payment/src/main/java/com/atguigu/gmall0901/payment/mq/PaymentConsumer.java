package com.atguigu.gmall0901.payment.mq;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall0901.bean.PaymentInfo;
import com.atguigu.gmall0901.bean.enums.ProcessStatus;
import com.atguigu.gmall0901.service.OrderService;
import com.atguigu.gmall0901.service.PaymentService;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;

@Component
public class PaymentConsumer {

    @Reference
    private PaymentService paymentService;

    //监听消息
    @JmsListener(destination = "PAYMENT_RESULT_CHECK_QUEUE",containerFactory = "jmsQueueListener")
    public void consumeSkuDeduct(ActiveMQMapMessage activeMQMapMessage) throws JMSException {
        //获取消息队列中的数据
        String outTradeNo = activeMQMapMessage.getString("outTradeNo");
        int delaySec = activeMQMapMessage.getInt("delaySec");
        int checkCount = activeMQMapMessage.getInt("checkCount");

        //1. 根据outTradeNo查询支付结果
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setOutTradeNo(outTradeNo);
        boolean flag= paymentService.checkPayment(paymentInfo);

        System.out.println("检查结果：" + flag);
        //每15秒检查一次  总共检查三次
        if(!flag && checkCount > 0){
            System.out.println("检查次数：" + checkCount);
            //说明用户没有付款  继续等待 到了时间会主动再次询问支付结果
            paymentService.sendDelayPaymentResult(outTradeNo,delaySec,checkCount - 1);
        }
    }
}
