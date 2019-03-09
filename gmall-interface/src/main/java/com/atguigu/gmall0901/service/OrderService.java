package com.atguigu.gmall0901.service;

import com.atguigu.gmall0901.bean.OrderInfo;
import com.atguigu.gmall0901.bean.enums.ProcessStatus;

import java.util.List;
import java.util.Map;

public interface OrderService {

    /**
     * 保存订单信息
     * @param orderInfo
     * @return
     */
    public  String  saveOrder(OrderInfo orderInfo);

    /**
     * 生成流水号
     * @param userId
     * @return
     */
    String getTradeNo(String userId);


    /**
     * 比较流水号
     * @param userId
     * @param tradeCodeNo
     * @return
     */
    boolean checkTradeCode(String userId,String tradeCodeNo);


    /**
     * 删除流水号
     */
    void  delTradeCode(String userId);

    /**
     * 库存查询接口
     * @param skuId
     * @param skuNum
     * @return
     */
    boolean checkStock(String skuId, Integer skuNum);


    /**
     * 根据orderId查询OrderInfo对象
     * @param orderId
     * @return
     */
    OrderInfo getOrderInfo(String orderId);


    /**
     * 根据orderId删除orderinfo
     * @param orderId
     */
    void delOrderInfo(String orderId);


    /**
     * 修改订单的状态
     * @param orderId
     * @param processStatus
     */
    void updateOrderStatus(String orderId, ProcessStatus processStatus);


    /**
     * 根据orderId发送消息给库存
     * @param orderId
     */
    void sendOrderStatus(String orderId);


    /**
     * 查询过期订单
     * @return
     */
    List<OrderInfo> getExpiredOrderList();

    /**
     * 处理过期订单
     * @param orderInfo
     */
    void execExpiredOrder(OrderInfo orderInfo);

    /**
     * orderinfo转换为Map集合
     * @param orderInfo
     * @return
     */
    Map initWareOrder(OrderInfo orderInfo);


    /**
     * 拆单
     * @param orderId
     * @param wareSkuMap
     * @return
     */
    List<OrderInfo> splitOrder(String orderId, String wareSkuMap);
}
