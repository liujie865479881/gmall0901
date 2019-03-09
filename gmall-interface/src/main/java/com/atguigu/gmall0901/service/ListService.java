package com.atguigu.gmall0901.service;


import com.atguigu.gmall0901.bean.SkuLsInfo;
import com.atguigu.gmall0901.bean.SkuLsParams;
import com.atguigu.gmall0901.bean.SkuLsResult;

public interface ListService {


    /**
     * 保存数据
     * @param skuLsInfo
     */
    void saveSkuInfo(SkuLsInfo skuLsInfo);


    /**
     * 查询es中的数据
     * @param skuLsParams
     * @return
     */
    SkuLsResult search (SkuLsParams skuLsParams);


    /**
     * 计数器
     * @param skuId
     */
    void incrHotScore(String skuId);
}
