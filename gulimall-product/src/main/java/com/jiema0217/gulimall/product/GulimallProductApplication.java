package com.jiema0217.gulimall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 1、整合MybBatis-Plus
 *      1)、导入依赖
 *      2）、配置
 *          1、配置数据源
 *              1）、导入数据库的驱动
 *              2)、配置数据源
 *          2、配置MyBatis-Plus
 *              1）、使用@MapperScan
 *              2)、告诉MyBatis-Plus，sql映射文件位置
 * 2、逻辑删除
 *      1）、配置全局的逻辑删除规则（省略）
 *      2）、3.1以前版本配置逻辑删除的组件Bean（省略）
 *      3）、给Bean加上逻辑删除注解@TableLogic
 * 3、JSR303
 *      1）、给Bean添加校验注解:javax.validation.constraint，并定义自己的message提示
 *      2）、开启校验功能@Valid
 *          效果：校验错误以后会有默认的响应
 *      3）、给校验的bean后紧跟一个BindingResult，就可以获取到校验的结果
 *      4）、分组校验（多场景的复杂校验）
 *          1、@NotBlank(message = "XXXX", groups = {Xxxx.class, ...})
 *          给校验注解标注什么情况需要进行分组校验
 *          2、@Validated({Xxxx.class})
 *          3、默认没有指定分组校验注解，在分组校验情况下不生效，只会在没标注分组下生效
 *      5）、自定义校验
 *          1、编写一个自定义的校验注解
 *          2、编写一个自定义的校验器 ConstraintValidator
 *          3、关联自定义的校验器和自定义的校验注解
 * 4、统一的异常处理
 * @ControllerAdvice
 *      1）、编写异常处理类，使用@ControllerAdvice
 *      2）、使用@ExceptionHandler标注方法可以处理的异常
 *
 * 5、模板引擎
 *      1）、thymeleaf-starter：关闭缓存
 *      2）、静态资源都放在static文件夹下，可以按照路径直接访问
 *      3）、页面放在templas下，可以直接访问
 *          Spring Boot，访问项目默认会找index
 *      4）、页面修改不重启服务器实时更新
 *          1、引入dev-tools
 *          2、修改完页面使用 Ctrl+Shift+F9 重新自动编译页面，代码配置，推荐重启
 *
 * 6、整合redis
 *      1）、引入data-redis-starter
 *      2）、简单配置redis的host等信息
 *      3）、使用Spring-Boot自动配置好的StringRedisTemplate来操作redis
 *
 * 7、整合redisson作为分布式等功能框架
 *      1）、引入依赖
 *      2）、配置redisson
 *      3）、使用：参照文档
 *
 * 8、整合SpringCache简化缓存开发
 *      1）、引入依赖
 *      2）、写配置
 *          1、自动配置了什么
 *              CacheAutoConfiguration 会导入 RedisCacheConfiguration
 *              自动配好了缓存管理器
 *          2、配置使用redis作为缓存
 *          3、测试使用缓存
 *              1）、开启缓存功能
 *              2）、只需要使用注解就能完成缓存操作
 *          4、原理
 *              CacheAutoConfiguration -> RedisCacheConfiguration -> 自动配置了缓存管理器 RedisCacheManager —> 初始化所有的缓存
 *              -> 每个缓存决定使用什么配置 -> 如果 RedisCacheConfiguration 有就用已有的，没有就用默认配置
 *              -> 想改缓存中的配置，只需要给容器中放一个RedisCacheConfiguration即可 -> 就会用到当前缓存管理器RedisCacheManager管理的所有缓存分区中
 *
 */

@EnableFeignClients(basePackages = "com.jiema0217.gulimall.product.feign")
@EnableDiscoveryClient
@MapperScan("com.jiema0217.gulimall.product.dao")
@SpringBootApplication
public class GulimallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallProductApplication.class, args);
    }

}
