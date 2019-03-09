package com.atguigu.gmall0901.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.atguigu.gmall0901")
@MapperScan(basePackages = "com.atguigu.gmall0901.order.mapper")
public class GmallOrderServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GmallOrderServiceApplication.class, args);
	}

}

