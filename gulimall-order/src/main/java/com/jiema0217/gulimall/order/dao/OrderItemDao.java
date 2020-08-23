package com.jiema0217.gulimall.order.dao;

import com.jiema0217.gulimall.order.entity.OrderItemEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单项信息
 * 
 * @author KEKEXI
 * @email 924616655@qq.com
 * @date 2020-08-23 19:20:37
 */
@Mapper
public interface OrderItemDao extends BaseMapper<OrderItemEntity> {
	
}
