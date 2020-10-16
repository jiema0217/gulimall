package com.jiema0217.gulimall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 二级分类vo
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Catelog2Vo {
    /**
     * 一级父分类id
     */
    private String catalog1Id;

    /**
     * 三级子分类
     */

    private List<Catelog3Vo> catalog3List;

    /**
     * 当前节点的id
     */
    private String id;

    /**
     * 当前节点的名字
     */
    private String name;


    /**
     * 三级分类vo
     */
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Catelog3Vo {

        /**
         * 父分类二级id
         */
        private String catalog2Id;

        private String id;

        private String name;
    }
}
