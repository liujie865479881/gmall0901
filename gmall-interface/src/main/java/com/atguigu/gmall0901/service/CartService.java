package com.atguigu.gmall0901.service;

import com.atguigu.gmall0901.bean.CartInfo;

import java.util.List;

public interface CartService {


    /**
     * 添加购物车
     * @param skuId
     * @param userId
     * @param skuNum
     */
    void  addToCart(String skuId,String userId,Integer skuNum);


    /**
     *  根据用用户id
     * @param userId
     * @return
     */
    List<CartInfo> getCartList(String userId);


    /**
     * 合并购物车
     * @param cartListCK
     * @param userId
     * @return
     */
    List<CartInfo> mergeToCartList(List<CartInfo> cartListCK, String userId);


    /**
     * 选中状态
     *
     * @param skuId
     * @param isChecked
     * @param userId
     */
    void checkCart(String skuId, String isChecked, String userId);


    /**
     * 根据用户id查询被选中的购物车列表
     * @param userId
     * @return
     */
    List<CartInfo> getCartCheckedList(String userId);

    /**
     * 根据用户id删除购物车
     * @param userId
     */
    void delCartInfo(String userId);
}
