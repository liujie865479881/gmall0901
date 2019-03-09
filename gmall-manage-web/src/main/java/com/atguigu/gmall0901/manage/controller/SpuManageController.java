package com.atguigu.gmall0901.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall0901.bean.BaseSaleAttr;
import com.atguigu.gmall0901.bean.SpuImage;
import com.atguigu.gmall0901.bean.SpuInfo;
import com.atguigu.gmall0901.bean.SpuSaleAttr;
import com.atguigu.gmall0901.service.ManageService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class SpuManageController{

    @Reference
    private ManageService manageService;

    @RequestMapping("/spuListPage")
    public String spuListPage(){
        return "spuListPage";
    }

    @RequestMapping("/spuList")
    @ResponseBody
    public List<SpuInfo> spuList(String catalog3Id){
        SpuInfo spuInfo = new SpuInfo();
        spuInfo.setCatalog3Id(catalog3Id);
        return manageService.getSpuInfoList(spuInfo);
    }


    @RequestMapping("/baseSaleAttrList")
    @ResponseBody
    public List<BaseSaleAttr> baseSaleAttrList(){
        return manageService.getBaseSaleAttrList();
    }


    @RequestMapping("/saveSpuInfo")
    @ResponseBody
    public void saveSpuInfo(SpuInfo spuInfo){
        manageService.saveSpuInfo(spuInfo);
    }


    @RequestMapping("/spuImageList")
    @ResponseBody
    public List<SpuImage> spuImageList(String spuId){
        return manageService.getSpuImageList(spuId);
    }


    /**
     * 获取销售属性的集合
     */
    @RequestMapping("/spuSaleAttrList")
    @ResponseBody
    public List<SpuSaleAttr> spuSaleAttrList(String spuId){
        return manageService.getSpuSaleAttrList(spuId);
    }

}
