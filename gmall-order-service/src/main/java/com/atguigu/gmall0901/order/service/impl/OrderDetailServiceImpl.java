package com.atguigu.gmall0901.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall0901.bean.OrderDetail;
import com.atguigu.gmall0901.order.mapper.OrderDetailMapper;
import com.atguigu.gmall0901.service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.common.Mapper;

@Service
public class OrderDetailServiceImpl implements OrderDetailService {

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    /**
     * 根据orderID删除orderDetail
     * @param orderId
     */
    @Override
    public void delOrderDetail(String orderId) {

        OrderDetail orderDetail = new OrderDetail();

        orderDetail.setOrderId(orderId);

        orderDetailMapper.delete(orderDetail);
    }
}
