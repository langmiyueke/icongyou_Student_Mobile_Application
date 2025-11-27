package com.huangjinbin.back_end_src_code;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// 移除@MapperScan注解，避免与配置类重复扫描
@SpringBootApplication
public class BackEndSrcCodeApplication {
	public static void main(String[] args) {
		SpringApplication.run(BackEndSrcCodeApplication.class, args);
	}
}