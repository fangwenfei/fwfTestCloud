package com.cfmoto.bar.code;

import com.cfmoto.bar.code.config.FeignHystrixConcurrencyStrategy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableAsync
@SpringBootApplication
@EnableDiscoveryClient
@EnableSwagger2
@EnableTransactionManagement
@ComponentScan(basePackages = {"com.cfmoto.bar.code", "com.github.pig.common.bean"})
@EnableFeignClients
public class CfmotoBarCodeServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CfmotoBarCodeServiceApplication.class, args);
	}


	@Bean
	public FeignHystrixConcurrencyStrategy feignHystrixConcurrencyStrategy() {
		return new FeignHystrixConcurrencyStrategy();
	}
}

