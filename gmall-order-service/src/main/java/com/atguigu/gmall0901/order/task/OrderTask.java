package com.atguigu.gmall0901.order.task;


import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall0901.bean.OrderInfo;
import com.atguigu.gmall0901.service.OrderService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@EnableScheduling
@Component
public class OrderTask {

    @Reference
    private OrderService orderService;

    //开启定时任务
   /* @Scheduled(cron = "5 * * * * ?")
    public void test01(){
        System.out.println(Thread.currentThread().getName() + "-------test01-----");
    }

    @Scheduled(cron = "0/30 * * * * ?")
    public void test02(){
        System.out.println(Thread.currentThread().getName() + "-------test02-----");
    }*/


    @Scheduled(cron = "0/30 * * * * ?")
    public void checkOrder(){
        //查询过期订单有哪些
        List<OrderInfo> orderInfoList = orderService.getExpiredOrderList();
        
        //循环遍历过期订单 对过期订单处理
        if(orderInfoList != null && orderInfoList.size() > 0){
            for (OrderInfo orderInfo : orderInfoList) {
                //处理过期订单的方法
                orderService.execExpiredOrder(orderInfo);
            }
        }
    }
}
