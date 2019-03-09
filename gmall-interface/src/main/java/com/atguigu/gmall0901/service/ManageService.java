package com.atguigu.gmall0901.service;

import com.atguigu.gmall0901.bean.*;

import java.util.List;

public interface ManageService {

    /**
     * 查询一级分类
     */
    List<BaseCatalog1> getCatalog1();

    /**
     * 根据一级分类id查询二级分类
     */
    List<BaseCatalog2> getCatalog2(String catalog1Id);

    /**
     * 根据二级分类id查询三级分类
     */
    List<BaseCatalog3> getCatalog3(String catalog2Id);

    /**
     * 根据三级分类id查询商品属性
     */
    List<BaseAttrInfo> getAttrList(String catalog3Id);

    /**
     * 保存平台属性---平台属性值
     */
    void saveAttrInfo(BaseAttrInfo baseAttrInfo);

    /**
     * 根据attrId查找AttrInfo，获取该对象下的List<BaseAttrValue>
     * @param attrId
     * @return
     */
    BaseAttrInfo getAttrInfo(String attrId);


    /**
     * 根据catalog3Id获得spu列表
     * @param spuInfo
     * @return
     */
    List<SpuInfo> getSpuInfoList(SpuInfo spuInfo);


    /**
     * 查询基本销售属性表
     * @return
     */
    List<BaseSaleAttr> getBaseSaleAttrList();


    /**
     * 保存销售属性
     * @param spuInfo
     */
    void saveSpuInfo(SpuInfo spuInfo);


    /**
     * 查询所有图片
     */
    List<SpuImage> getSpuImageList(String spuId);

    /**
     * 查询销售属性 根据spuId
     */
    List<SpuSaleAttr> getSpuSaleAttrList(String spuId);


    /**
     * 保存商品skuInfo数据
     * @param skuInfo
     */
    void saveSkuInfo(SkuInfo skuInfo);


    /**
     * 通过skuId获取skuInfo
     */
    SkuInfo getSkuInfo(String skuId);


    /**
     * 根据spuId查询所有的销售属性，再根据skuId将商品的销售属性值，默认选中
     * @return
     */
    List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(SkuInfo skuInfo);


    /**
     * 根据spuId查询所有的skuId集合
     */
    List<SkuSaleAttrValue> getSkuSaleAttrValueListBySpu(String spuId);


    /**
     * 根据平台属性值id 查询平台属性
     * @param attrValueIdList
     * @return
     */
    List<BaseAttrInfo> getAttrList(List<String> attrValueIdList);
}
