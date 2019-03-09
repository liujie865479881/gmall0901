package com.atguigu.gmall0901.usermanage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall0901.bean.UserAddress;
import com.atguigu.gmall0901.bean.UserInfo;
import com.atguigu.gmall0901.config.RedisUtil;
import com.atguigu.gmall0901.service.UserService;
import com.atguigu.gmall0901.usermanage.mapper.UserAddressMapper;
import com.atguigu.gmall0901.usermanage.mapper.UserInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import redis.clients.jedis.Jedis;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    //实现类必须引用通用Mapper
    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private UserAddressMapper userAddressMapper;

    @Autowired
    private RedisUtil redisUtil;

    public String USERKEY_PREFIX="user:";
    public String USERINFOKEY_SUFFIX=":info";
    public int USERKEY_TIMEOUT=60*60*24;


    @Override
    public List<UserInfo> findAll() {
        return userInfoMapper.selectAll();
    }

    @Override
    public List<UserAddress> findUserAddressByUserId(String userId) {
        UserAddress userAddress = new UserAddress();
        userAddress.setUserId(userId);
        return userAddressMapper.select(userAddress);
    }

    /**
     * 登录
     * @param userInfo
     * @return
     */
    @Override
    public UserInfo login(UserInfo userInfo) {
        //获取jedis
        Jedis jedis = redisUtil.getJedis();
        //密码要加密
        String passwd = userInfo.getPasswd();
        //加密
        String newPassword = DigestUtils.md5DigestAsHex(passwd.getBytes());
        //将加密的密码赋给用户对象
        userInfo.setPasswd(newPassword);

        UserInfo info = userInfoMapper.selectOne(userInfo);
        //将用户信息存放到redis
        //定义key
        String userKey = USERKEY_PREFIX + info.getId() + USERINFOKEY_SUFFIX;
        if(info != null){
            //放入redis
            jedis.setex(userKey,USERKEY_TIMEOUT, JSON.toJSONString(info));
            jedis.close();
            return info;
        }
        return null;
    }


    /**
     * 根据用户id认证redis中是否存在用户信息
     * @param userId
     */
    @Override
    public UserInfo verify(String userId) {
        //先获取jedis
        Jedis jedis = redisUtil.getJedis();

        //定义key
        String userKey = USERKEY_PREFIX + userId + USERINFOKEY_SUFFIX;
        //获取数据
        String userJson = jedis.get(userKey);
        //将字符串转化为对象
        if(userJson != null && userJson.length() > 0){
            //如果得到当前用户  延长一下用户过期时间
            jedis.expire(userKey,USERKEY_TIMEOUT);

            UserInfo userInfo = JSON.parseObject(userJson,UserInfo.class);
            return userInfo;
        }

        return null;
    }
}
