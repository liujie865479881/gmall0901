package com.atguigu.gmall0901.payment.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.atguigu.gmall0901.bean.PaymentInfo;
import com.atguigu.gmall0901.bean.enums.PaymentStatus;
import com.atguigu.gmall0901.config.ActiveMQUtil;
import com.atguigu.gmall0901.payment.mapper.PaymentInfoMapper;
import com.atguigu.gmall0901.service.PaymentService;
import org.apache.activemq.ScheduledMessage;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import javax.jms.*;
import java.util.HashMap;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentInfoMapper paymentInfoMapper;

    @Autowired
    private ActiveMQUtil activeMQUtil;

    @Autowired
    private AlipayClient alipayClient;

    /**
     * 保存交易记录
     * @param paymentInfo
     */
    @Override
    public void savePaymentInfo(PaymentInfo paymentInfo) {

        paymentInfoMapper.insertSelective(paymentInfo);
    }


    /**
     * 根据out_trade_no查询PaymentInfo对象
     * @param paymentInfoQuery
     * @return
     */
    @Override
    public PaymentInfo getPaymentInfo(PaymentInfo paymentInfoQuery) {

        PaymentInfo paymentInfo = paymentInfoMapper.selectOne(paymentInfoQuery);
        return paymentInfo;
    }



    /**
     * 修改交易记录状态
     * @param out_trade_no
     * @param paymentInfoUpd
     */
    @Override
    public void updatePaymentInfo(String out_trade_no, PaymentInfo paymentInfoUpd) {

        Example example = new Example(PaymentInfo.class);
        //构造修改条件
        example.createCriteria().andEqualTo("outTradeNo",out_trade_no);

        paymentInfoMapper.updateByExampleSelective(paymentInfoUpd,example);
    }

    /**
     * 发送支付结果的消息
     * @param paymentInfo
     * @param result
     */
    @Override
    public void sendPaymentResult(PaymentInfo paymentInfo, String result) {
        //获取工厂
        Connection connection = activeMQUtil.getConnection();

        //打开连接
        try {
            connection.start();

            //创建session
            Session session = connection.createSession(true, Session.SESSION_TRANSACTED);

            //创建消息队列
            Queue payment_result_queue = session.createQueue("PAYMENT_RESULT_QUEUE");

            //创建消息提供者
            MessageProducer producer = session.createProducer(payment_result_queue);

            //创建消息对象
            ActiveMQMapMessage activeMQMapMessage = new ActiveMQMapMessage();

            activeMQMapMessage.setString("orderId",paymentInfo.getOrderId());
            activeMQMapMessage.setString("result",result);

            //发送消息
            producer.send(activeMQMapMessage);

            session.commit();

            producer.close();
            session.close();
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }


    /**
     * 查询订单的支付状态
     * @param paymentInfoQuery
     * @return
     */
    @Override
    public boolean checkPayment(PaymentInfo paymentInfoQuery) {

        //AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do","app_id","your private_key","json","GBK","alipay_public_key","RSA2");
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();

        HashMap<Object, Object> map = new HashMap<>();
        map.put("out_trade_no", paymentInfoQuery.getOutTradeNo());

        //将out_trade_no变成json字符串放入方法中
        request.setBizContent(JSON.toJSONString(map));


        /*request.setBizContent("{" +
                "\"out_trade_no\":\"20150320010101001\"," +
                "\"trade_no\":\"2014112611001004680 073956707\"," +
                "\"org_pid\":\"2088101117952222\"" +
                "  }");*/
        AlipayTradeQueryResponse response = null;
        try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        //调用成功
        if(response.isSuccess()){
            //看交易状态
            if("TRADE_SUCCESS".equals(response.getTradeStatus()) || "TRADE_FINISHED".equals(response.getTradeStatus())){
                //支付成功  更改状态
                PaymentInfo paymentInfoUpd = new PaymentInfo();
                paymentInfoUpd.setPaymentStatus(PaymentStatus.PAID);

                updatePaymentInfo(paymentInfoQuery.getOutTradeNo(),paymentInfoUpd);
                //发送通知给订单模块
                sendPaymentResult(paymentInfoQuery, "result");

                return true;
            }
        } else {
            return false;
        }

        return false;
    }

    /**
     * 发送消息 延时队列接口
     * @param outTradeNo
     * @param delaySec
     * @param checkCount
     */
    @Override
    public void sendDelayPaymentResult(String outTradeNo, int delaySec, int checkCount) {

        //创建连接工厂 获取连接
        Connection connection = activeMQUtil.getConnection();

        try {
            connection.start();

            Session session = connection.createSession(true, Session.SESSION_TRANSACTED);

            Queue payment_result_check_queue = session.createQueue("PAYMENT_RESULT_CHECK_QUEUE");

            MessageProducer producer = session.createProducer(payment_result_check_queue);

            ActiveMQMapMessage activeMQMapMessage = new ActiveMQMapMessage();

            activeMQMapMessage.setString("outTradeNo", outTradeNo);
            activeMQMapMessage.setInt("delaySec", delaySec);
            activeMQMapMessage.setInt("checkCount", checkCount);

            //设置延迟时间
            activeMQMapMessage.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, delaySec * 1000);
            producer.send(activeMQMapMessage);

            session.commit();

            producer.close();
            session.close();
            connection.close();

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }


    /**
     * 根据orderId关闭过期的交易记录
     * @param id
     */
    @Override
    public void closePayment(String id) {

        Example example = new Example(PaymentInfo.class);

        example.createCriteria().andEqualTo("orderId",id);

        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setPaymentStatus(PaymentStatus.ClOSED);

        paymentInfoMapper.updateByExampleSelective(paymentInfo,example);
    }
}
