package com.jiema0217.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiema0217.common.utils.PageUtils;
import com.jiema0217.gulimall.member.entity.IntegrationChangeHistoryEntity;

import java.util.Map;

/**
 * 积分变化历史记录
 *
 * @author KEKEXI
 * @email 924616655@qq.com
 * @date 2020-08-23 19:12:32
 */
public interface IntegrationChangeHistoryService extends IService<IntegrationChangeHistoryEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

