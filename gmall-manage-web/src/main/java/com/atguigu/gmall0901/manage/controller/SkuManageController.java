package com.atguigu.gmall0901.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall0901.bean.SkuInfo;
import com.atguigu.gmall0901.bean.SkuLsInfo;
import com.atguigu.gmall0901.service.ListService;
import com.atguigu.gmall0901.service.ManageService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SkuManageController {

    @Reference
    private ManageService manageService;

    @Reference
    private ListService listService;


    @RequestMapping("/saveSkuInfo")
    @ResponseBody
    public void saveSkuInfo(SkuInfo skuInfo){
        manageService.saveSkuInfo(skuInfo);
    }


    @RequestMapping("onSale")
    @ResponseBody
    public String onSale(String skuId){
        //通过skuId得到SkuInfo
        SkuInfo skuInfo = manageService.getSkuInfo(skuId);

        //给skuLsInfo赋值
        SkuLsInfo skuLsInfo = new SkuLsInfo();

        //使用工具类 可以属性拷贝
        BeanUtils.copyProperties(skuInfo, skuLsInfo);

        listService.saveSkuInfo(skuLsInfo);
        return "OK";
    }
}
