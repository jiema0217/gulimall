package com.jiema0217.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiema0217.common.utils.PageUtils;
import com.jiema0217.gulimall.product.entity.BrandEntity;

import java.util.List;
import java.util.Map;

/**
 * 品牌
 *
 * @author KEKEXI
 * @email 924616655@qq.com
 * @date 2020-08-23 18:06:29
 */
public interface BrandService extends IService<BrandEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void updateDetail(BrandEntity brand);

    List<BrandEntity> getBrandsByIds(List<Long> brandIds);
}

