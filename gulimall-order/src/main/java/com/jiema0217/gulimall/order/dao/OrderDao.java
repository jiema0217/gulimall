package com.jiema0217.gulimall.order.dao;

import com.jiema0217.gulimall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author KEKEXI
 * @email 924616655@qq.com
 * @date 2020-08-23 19:20:37
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
