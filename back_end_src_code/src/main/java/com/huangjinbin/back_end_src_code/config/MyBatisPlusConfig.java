package com.huangjinbin.back_end_src_code.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 配置类（分页插件 + Mapper扫描）
 */
@Configuration
@MapperScan("com.huangjinbin.back_end_src_code.mapper") // 唯一的Mapper扫描入口
public class MyBatisPlusConfig {

    /**
     * 注册分页插件，支持分页查询
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 添加MySQL分页拦截器
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(com.baomidou.mybatisplus.annotation.DbType.MYSQL));
        return interceptor;
    }
}