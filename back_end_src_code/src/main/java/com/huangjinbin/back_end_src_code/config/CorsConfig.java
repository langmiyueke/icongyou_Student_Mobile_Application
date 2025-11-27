package com.huangjinbin.back_end_src_code.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 跨域配置类（允许前端域名访问后端接口）
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        // 1. 配置跨域信息
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("*"); // 允许所有前端域名（生产环境需指定具体域名）
        config.setAllowCredentials(true); // 允许携带Cookie
        config.addAllowedMethod("*"); // 允许所有HTTP方法（GET、POST等）
        config.addAllowedHeader("*"); // 允许所有请求头

        // 2. 配置跨域路径匹配规则
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // 所有接口都支持跨域

        return new CorsFilter(source);
    }
}