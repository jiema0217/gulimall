package com.jiema0217.gulimall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
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
 *      1）、
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
