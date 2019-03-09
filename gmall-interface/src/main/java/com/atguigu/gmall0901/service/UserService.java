package com.atguigu.gmall0901.service;

import com.atguigu.gmall0901.bean.UserAddress;
import com.atguigu.gmall0901.bean.UserInfo;

import java.util.List;

public interface UserService {

    /**
     * 查询所有用户
     */
    List<UserInfo> findAll();


    /**
     * 根据用户id查询用户的地址
     */
    List<UserAddress> findUserAddressByUserId(String userId);


    /**
     * 登录
     * @param userInfo
     * @return
     */
    UserInfo login(UserInfo userInfo);


    /**
     * 根据用户id认证redis中是否存在用户信息
     * @param userId
     */
    UserInfo verify(String userId);
}
