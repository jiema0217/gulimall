package com.jiema0217.gulimall.product;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jiema0217.gulimall.product.entity.BrandEntity;
import com.jiema0217.gulimall.product.service.BrandService;
import com.jiema0217.gulimall.product.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

/**
 * 1、引入oss-starter
 * 2、配置key、endpoint相关信息即可
 * 3、使用OSSClient进行相关操作
 */
@Slf4j
@SpringBootTest
class GulimallProductApplicationTests {

    @Autowired
    BrandService brandService;

    @Autowired
    CategoryService categoryService;

    @Test
    public void testFindPath() {
        Long[] catelogPath = categoryService.findCatelogPath(225L);
        log.info("完整路径:{}", Arrays.asList(catelogPath));
    }

//    @Autowired
//    OSSClient ossClient;
//
//    @Test
//    public void testUpload() throws FileNotFoundException {
//
////// Endpoint以杭州为例，其它Region请按实际情况填写。
////        String endpoint = "oss-cn-beijing.aliyuncs.com";
////// 云账号AccessKey有所有API访问权限，建议遵循阿里云安全最佳实践，创建并使用RAM子账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建。
////        String accessKeyId = "LTAI4GAy9BspyF6sKGTVMT5K";
////        String accessKeySecret = "DgT9eDqnM96QrQfsTSiU7DTn4Sd5wD";
////
////// 创建OSSClient实例。
////        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
//
//// 上传文件流。
//        InputStream inputStream = new FileInputStream("F:\\BaiduNetdiskDownload\\谷粒商城\\1-分布式基础_全栈开发篇\\docs\\pics\\63e862164165f483.jpg");
//        ossClient.putObject("gulimall-hello7", "63e862164165f483.jpg", inputStream);
//
//// 关闭OSSClient。
//        ossClient.shutdown();
//        System.out.println("上传完成...");
//    }

    @Test
    void contextLoads() {

        List<BrandEntity> list = brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id", 1L));
        list.forEach((item) -> System.out.println(item));
    }

}
