package com.jiema0217.gulimall.product;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jiema0217.gulimall.product.entity.BrandEntity;
import com.jiema0217.gulimall.product.service.BrandService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class GulimallProductApplicationTests {

    @Autowired
    BrandService brandService;
    @Test
    void contextLoads() {

        List<BrandEntity> list = brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id",1L));
        list.forEach((item)-> System.out.println(item));
    }

}
