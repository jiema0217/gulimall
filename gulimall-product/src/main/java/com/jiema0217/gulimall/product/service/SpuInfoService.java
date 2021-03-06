package com.jiema0217.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiema0217.common.utils.PageUtils;
import com.jiema0217.gulimall.product.entity.SpuInfoDescEntity;
import com.jiema0217.gulimall.product.entity.SpuInfoEntity;
import com.jiema0217.gulimall.product.vo.SpuSaveVo;

import java.util.Map;

/**
 * spu信息
 *
 * @author KEKEXI
 * @email 924616655@qq.com
 * @date 2020-08-23 18:06:29
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSpuInfo(SpuSaveVo vo);

    void saveBaseSpuInfo(SpuInfoEntity infoEntity);

    PageUtils queryPageByCondition(Map<String, Object> params);

    /**
     * 商品上架
     * @param spuId
     */
    void up(Long spuId);
}

