package com.atguigu.gmall0901.passport.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall0901.bean.UserInfo;
import com.atguigu.gmall0901.service.UserService;
import config.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PassportController {

    @Value("${token.key}")
    private String key;

    @Reference
    private UserService userService;

    @RequestMapping("index")
    public String index(HttpServletRequest request){
        String originUrl = request.getParameter("originUrl");
        //存储originUrl
        request.setAttribute("originUrl",originUrl);
        return "index";
    }



    @RequestMapping("login")
    @ResponseBody
    public String login(UserInfo userInfo, HttpServletRequest request){
        //从nginx中获取
        String salt = request.getHeader("X-forwarded-for");
        //得到用户名和密码  调用服务层进行验证
        UserInfo info = userService.login(userInfo);
        if(info != null){
            //key放入配置文件中
            //String key = "atguigu";
            HashMap<String, Object> map = new HashMap<>();
            map.put("userId",info.getId());
            map.put("nickName",info.getNickName());
           // System.out.println(info.getNickName());
            //从服务器获取IP地址
            String token = JwtUtil.encode(key, map, salt);
           // System.out.println(token);
            return token;
        }else {
            return "fail";
        }
    }


    @RequestMapping("verify")
    @ResponseBody
    public String verify(HttpServletRequest request){
        //解密token  需要salt和key和token
        //key 配置文件获取  salt,token可以通过request得到
        String token = request.getParameter("token");
        String salt = request.getParameter("salt");

        //jwtUtil工具
        Map<String, Object> map = JwtUtil.decode(token, key, salt);

        if(map != null && map.size() > 0){
            //能够获取用户id
            String userId = (String) map.get("userId");
            //调用服务层查询用户是否存在
            UserInfo userInfo = userService.verify(userId);
            if(userInfo != null){
                return "success";
            }else {
                return "fail";
            }

        }
        return "fail";
    }
}
