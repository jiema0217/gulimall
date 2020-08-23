package com.jiema0217.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiema0217.common.utils.PageUtils;
import com.jiema0217.gulimall.order.entity.OrderEntity;

import java.util.Map;

/**
 * 订单
 *
 * @author KEKEXI
 * @email 924616655@qq.com
 * @date 2020-08-23 19:20:37
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

