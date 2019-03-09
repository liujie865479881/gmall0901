package com.atguigu.gmall0901.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall0901.bean.CartInfo;
import com.atguigu.gmall0901.bean.SkuInfo;
import com.atguigu.gmall0901.config.CookieUtil;
import com.atguigu.gmall0901.config.LoginRequire;
import com.atguigu.gmall0901.service.CartService;
import com.atguigu.gmall0901.service.ManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CartController {

    @Reference
    private CartService cartService;

    @Reference
    private ManageService manageService;

    @Autowired
    private CartCookieHandler cartCookieHandler;

    @RequestMapping("addToCart")
    @LoginRequire(autoRedirect = false)
    public String addToCart(HttpServletRequest request, HttpServletResponse response){

        //获取userId 通过拦截器获取
        String userId = (String) request.getAttribute("userId");

        String skuNum = request.getParameter("skuNum");
        String skuId = request.getParameter("skuId");

        if(userId != null){
            //已登录
            cartService.addToCart(skuId, userId,Integer.parseInt(skuNum));
        }else {
            //未登录  将数据添加到cookie中
            cartCookieHandler.addToCart(request,response,skuId,userId,Integer.parseInt(skuNum));

        }
        SkuInfo skuInfo = manageService.getSkuInfo(skuId);

        request.setAttribute("skuInfo",skuInfo);
        request.setAttribute("skuNum",skuNum);
        return "success";
    }


    @RequestMapping("cartList")
    @LoginRequire(autoRedirect = false)
    public String cartList(HttpServletRequest request, HttpServletResponse response){

        //获取userId 通过拦截器获取
        String userId = (String) request.getAttribute("userId");

        if(userId != null){
            //合并购物车 cookie合并给redis
            //获取cookie数据
            List<CartInfo> cartInfoList = new ArrayList<>();
            List<CartInfo> cartListCK = cartCookieHandler.getCartList(request);

            if(cartListCK != null && cartListCK.size() > 0){
                //合并购物车
                cartInfoList = cartService.mergeToCartList(cartListCK,userId);

                //合并之后 cookie中的数据没用  删除
                cartCookieHandler.deleteCartCookie(request,response);
            }else {
                //查询redis
                cartInfoList = cartService.getCartList(userId);
            }
            //保存
            request.setAttribute("cartInfoList",cartInfoList);
        }else {
            //查询cookie
            List<CartInfo> cartInfoList = cartCookieHandler.getCartList(request);

            //保存
            request.setAttribute("cartInfoList",cartInfoList);
        }

        return "cartList";
    }


    @RequestMapping("/checkCart")
    @LoginRequire(autoRedirect = false)
    public void checkCart(HttpServletRequest request, HttpServletResponse response){
        //获取skuId,isChecked,userId
        String userId = (String) request.getAttribute("userId");
        String isChecked = request.getParameter("isChecked");
        String skuId = request.getParameter("skuId");

        if(userId != null){
            //登录状态
            cartService.checkCart(skuId,isChecked,userId);
        }else {
            //未登录状态
            cartCookieHandler.checkCart(request,response,skuId,isChecked);
        }
    }


    @RequestMapping("/toTrade")
    @LoginRequire(autoRedirect = true)
    public String toTrade(HttpServletRequest request, HttpServletResponse response){
        //需要对选中商品的状态进行合并
        //得到cookie中的数据 合并到redis
        List<CartInfo> cartListCK = cartCookieHandler.getCartList(request);
        String userId = (String) request.getAttribute("userId");
        if(cartListCK != null && cartListCK.size() > 0){
            //合并
            cartService.mergeToCartList(cartListCK,userId);
            //合并之后 将cookie中的数据删除
            cartCookieHandler.deleteCartCookie(request,response);
        }
        //重定向到订单页面
        return "redirect://order.gmall.com/trade";
    }

}
