package com.atguigu.gmall0901.manage.mapper;

import com.atguigu.gmall0901.bean.SkuSaleAttrValue;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SkuSaleAttrValueMapper extends Mapper<SkuSaleAttrValue> {

    /**
     * 查询所有的SkuSaleAttrValue 根据 spuId
     * @param spuId
     * @return
     */
    List<SkuSaleAttrValue> selectSpuSaleAttrListCheckBySku(String spuId);
}
