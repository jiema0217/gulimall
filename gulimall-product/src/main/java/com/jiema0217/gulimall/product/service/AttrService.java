package com.jiema0217.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiema0217.common.utils.PageUtils;
import com.jiema0217.gulimall.product.entity.AttrEntity;
import com.jiema0217.gulimall.product.vo.AttrGroupRelationVo;
import com.jiema0217.gulimall.product.vo.AttrRespVo;
import com.jiema0217.gulimall.product.vo.AttrVo;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author KEKEXI
 * @email 924616655@qq.com
 * @date 2020-08-23 18:06:29
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveAttr(AttrVo attr);

    PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String type);

    AttrRespVo getAttrInfo(Long attrId);

    void updateAttr(AttrVo attr);

    List<AttrEntity> getRelationAttr(Long attrgroupId);

    void deletRelation(AttrGroupRelationVo... vos);

    PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId);
}

