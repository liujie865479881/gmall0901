package com.atguigu.gmall0901.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall0901.bean.SkuInfo;
import com.atguigu.gmall0901.bean.SkuSaleAttrValue;
import com.atguigu.gmall0901.bean.SpuSaleAttr;
import com.atguigu.gmall0901.config.LoginRequire;
import com.atguigu.gmall0901.service.ListService;
import com.atguigu.gmall0901.service.ManageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;

@Controller
public class ItemController {

    @Reference
    private ManageService manageService;

    @Reference
    private ListService listService;

    @RequestMapping("{skuId}.html")
    //@LoginRequire(autoRedirect = true)
    public String skuInfoPage(@PathVariable(value = "skuId") String skuId, Model model){
        SkuInfo skuInfo = manageService.getSkuInfo(skuId);

        //获取销售属性
        List<SpuSaleAttr> spuSaleAttrList = manageService.getSpuSaleAttrListCheckBySku(skuInfo);

        //获取销售属性值ID集合
        List<SkuSaleAttrValue> skuSaleAttrValueListBySpu = manageService.getSkuSaleAttrValueListBySpu(skuInfo.getSpuId());

        //定义一个字符串
        String key = "";
        HashMap<String, Object> map = new HashMap<>();
        for (int i = 0; i < skuSaleAttrValueListBySpu.size(); i++) {
            SkuSaleAttrValue skuSaleAttrValue = skuSaleAttrValueListBySpu.get(i);

            if(key.length() > 0){
                key += "|";
            }

            key += skuSaleAttrValue.getSaleAttrValueId();

            if((i + 1) == skuSaleAttrValueListBySpu.size() || !skuSaleAttrValue.getSkuId().equals(skuSaleAttrValueListBySpu.get(i + 1).getSkuId())){
                map.put(key,skuSaleAttrValue.getSkuId());
                key = "";
            }

        }

        //将map转化为json串
        String valuesSkuJson = JSON.toJSONString(map);

        //保存到作用域中
        model.addAttribute("valuesSkuJson",valuesSkuJson);


        //页面渲染
        model.addAttribute("skuInfo",skuInfo);
        model.addAttribute("spuSaleAttrList",spuSaleAttrList);

        //计数器
        listService.incrHotScore(skuId);

        return "item";
    }
}
