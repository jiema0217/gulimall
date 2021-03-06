package com.jiema0217.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiema0217.common.utils.PageUtils;
import com.jiema0217.gulimall.coupon.entity.CouponSpuRelationEntity;

import java.util.Map;

/**
 * 优惠券与产品关联
 *
 * @author KEKEXI
 * @email 924616655@qq.com
 * @date 2020-08-23 19:07:35
 */
public interface CouponSpuRelationService extends IService<CouponSpuRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

