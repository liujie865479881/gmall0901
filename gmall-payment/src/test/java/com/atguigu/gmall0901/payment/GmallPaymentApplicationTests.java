package com.atguigu.gmall0901.payment;

import com.atguigu.gmall0901.config.ActiveMQUtil;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.jms.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallPaymentApplicationTests {

	@Autowired
	private ActiveMQUtil activeMQUtil;


	@Test
	public void testActiveMq() throws JMSException {
		Connection connection = activeMQUtil.getConnection();

		connection.start();

		//创建会话session  第一参数：是否开启事务  第二参数：根据第一参数 选择手动签收事务还是自动签收
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		//创建队列
		Queue queue = session.createQueue("activemqTest1");

		//将队列放入提供者对象
		MessageProducer producer = session.createProducer(queue);

		//提供者对象发送消息
		//创建消息对象
		ActiveMQTextMessage activeMQTextMessage = new ActiveMQTextMessage();
		activeMQTextMessage.setText("随便写点 测试si下");

		producer.send(activeMQTextMessage);
		//session.commit();

		//关闭
		producer.close();
		session.close();
		connection.close();

	}




	@Test
	public void contextLoads() {
	}

}

