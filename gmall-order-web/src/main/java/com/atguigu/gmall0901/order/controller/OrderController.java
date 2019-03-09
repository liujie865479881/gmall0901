package com.atguigu.gmall0901.order.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall0901.bean.CartInfo;
import com.atguigu.gmall0901.bean.OrderDetail;
import com.atguigu.gmall0901.bean.OrderInfo;
import com.atguigu.gmall0901.bean.UserAddress;
import com.atguigu.gmall0901.config.LoginRequire;
import com.atguigu.gmall0901.service.CartService;
import com.atguigu.gmall0901.service.OrderService;
import com.atguigu.gmall0901.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class OrderController {

    @Reference
    private UserService userService;

    @Reference
    private CartService cartService;

    @Reference
    private OrderService orderService;

    //根据用户id查询用户的地址
   /* @RequestMapping("/trade")
    @ResponseBody
    public List<UserAddress> trade(String userId){
        return userService.findUserAdderssByUserId(userId);
    }*/


    @RequestMapping("/trade")
    @LoginRequire(autoRedirect = true)
    public String trade(HttpServletRequest request){

        String userId = (String) request.getAttribute("userId");

        //根据用户id获取用户信息
        List<UserAddress> userAddressList = userService.findUserAddressByUserId(userId);

        //先获取购物车中被选中的商品列表
        List<CartInfo> cartInfoList = cartService.getCartCheckedList(userId);
        
        //声明一个订单明细的集合
        List<OrderDetail> orderDetailList = new ArrayList<>();

        //循环得到的商品列表 将里面的值赋给OrderDetail
        if(cartInfoList != null && cartInfoList.size() > 0){
            //赋值
            for (CartInfo cartInfo : cartInfoList) {
                //创建订单明细
                OrderDetail orderDetail = new OrderDetail();

                orderDetail.setSkuId(cartInfo.getSkuId());
                orderDetail.setSkuName(cartInfo.getSkuName());
                orderDetail.setImgUrl(cartInfo.getImgUrl());
                orderDetail.setSkuNum(cartInfo.getSkuNum());
                orderDetail.setOrderPrice(cartInfo.getCartPrice());

                orderDetailList.add(orderDetail);

            }
        }

        //总价格
        OrderInfo orderInfo = new OrderInfo();
        //将订单明细集合赋值给orderInfo
        orderInfo.setOrderDetailList(orderDetailList);
        //调用sumTotalAmount 会将计算结果赋值给orderInfo.totalAmount
        orderInfo.sumTotalAmount();
        //保存总价格
        request.setAttribute("totalAmount",orderInfo.getTotalAmount());

        //保存订单明细的集合对象
        request.setAttribute("orderDetailList",orderDetailList);

        //保存集合
        request.setAttribute("userAddressList",userAddressList);

        //生成一个流水号  将userID作为key储存编号
        String tradeNo = orderService.getTradeNo(userId);
        request.setAttribute("tradeNo",tradeNo);

        return "trade";
    }


    @RequestMapping("/submitOrder")
    @LoginRequire
    private String submitOrder(OrderInfo orderInfo, HttpServletRequest request){
        //获取userId
        String userId = (String) request.getAttribute("userId");
        orderInfo.setUserId(userId);

        //获取tradeNo
        String tradeNo = request.getParameter("tradeNo");

        //调用比较方法

        boolean result = orderService.checkTradeCode(userId, tradeNo);

        //比较失败
        if(!result){
           request.setAttribute("errMsg","不能重复提交订单!");
           return "tradeFail";
        }

        //验证库存
        List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();
        if (orderDetailList != null && orderDetailList.size() > 0){
            for (OrderDetail orderDetail : orderDetailList) {
                //验证每一个商品是否有足够库存
                boolean flag = orderService.checkStock(orderDetail.getSkuId(), orderDetail.getSkuNum());
                if(!flag){
                    request.setAttribute("errMsg","库存不足，请重新下单!");
                    return "tradeFail";
                }
            }
        }

        //计算总金额
        orderInfo.sumTotalAmount();

        orderInfo.setTotalAmount(orderInfo.getTotalAmount());
        String orderId = orderService.saveOrder(orderInfo);

        //删除redis中的流水号
        orderService.delTradeCode(userId);

        return "redirect://payment.gmall.com/index?orderId=" + orderId;
    }


    @RequestMapping("orderSplit")
    @ResponseBody
    public String orderSplit(HttpServletRequest request){

        //获取orderId wareSkuMap
        String orderId = request.getParameter("orderId");
        String wareSkuMap = request.getParameter("wareSkuMap");

        //获取子订单的集合
        List<OrderInfo> subOrderInfoList = orderService.splitOrder(orderId,wareSkuMap);

        //声明一个集合  来存储map集合
        ArrayList<Map> arrayList = new ArrayList<>();
        //将每个orderInfo对象转化为map
        for (OrderInfo orderInfo : subOrderInfoList) {
            Map map = orderService.initWareOrder(orderInfo);
            arrayList.add(map);
        }

        return JSON.toJSONString(arrayList);
    }
}
