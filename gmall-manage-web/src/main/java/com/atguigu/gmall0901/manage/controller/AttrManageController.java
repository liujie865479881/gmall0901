package com.atguigu.gmall0901.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall0901.bean.*;
import com.atguigu.gmall0901.service.ManageService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class AttrManageController {

    @Reference
    private ManageService manageService;

    @RequestMapping("/attrListPage")
    public String attrListPage(){
        return "attrListPage";
    }

    /**
     * 获取一级分类
     */
    @RequestMapping("/getCatalog1")
    @ResponseBody
    public List<BaseCatalog1> getCatalog1(){
        return manageService.getCatalog1();
    }

    /**
     * 获取二级分类
     */
    @RequestMapping("/getCatalog2")
    @ResponseBody
    public List<BaseCatalog2> getCatalog2(String catalog1Id){

        return manageService.getCatalog2(catalog1Id);
    }

    /**
     * 获取三级分类
     */
    @RequestMapping("/getCatalog3")
    @ResponseBody
    public List<BaseCatalog3> getCatalog3(String catalog2Id){

        return manageService.getCatalog3(catalog2Id);
    }

    /**
     * 获取商品信息
     */
    @RequestMapping("/attrInfoList")
    @ResponseBody
    public List<BaseAttrInfo> attrInfoList(String catalog3Id){
        return manageService.getAttrList(catalog3Id);
    }

    /**
     * 保存功能
     */
    @RequestMapping("/saveAttrInfo")
    @ResponseBody
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo){
        //调用服务层做保存方法
        manageService.saveAttrInfo(baseAttrInfo);
    }


    /**
     * 获取商品属性的集合
     */
    @RequestMapping("/getAttrValueList")
    @ResponseBody
    public List<BaseAttrValue> getAttrValueList(String attrId){
        BaseAttrInfo attrInfo = manageService.getAttrInfo(attrId);
        List<BaseAttrValue> attrValueList = attrInfo.getAttrValueList();
        System.out.println(attrValueList);
        return attrValueList;
    }


}
