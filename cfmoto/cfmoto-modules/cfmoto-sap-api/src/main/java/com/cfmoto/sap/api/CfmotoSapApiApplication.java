package com.cfmoto.sap.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class CfmotoSapApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(CfmotoSapApiApplication.class, args);
	}

}
