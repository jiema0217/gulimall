package com.jiema0217.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiema0217.common.utils.PageUtils;
import com.jiema0217.gulimall.product.entity.SkuImagesEntity;

import java.util.Map;

/**
 * sku图片
 *
 * @author KEKEXI
 * @email 924616655@qq.com
 * @date 2020-08-23 18:06:29
 */
public interface SkuImagesService extends IService<SkuImagesEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

