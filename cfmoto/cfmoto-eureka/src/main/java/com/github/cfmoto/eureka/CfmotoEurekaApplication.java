

package com.github.cfmoto.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @author lengleng
 */
@EnableEurekaServer
@SpringBootApplication
public class CfmotoEurekaApplication {

	public static void main(String[] args) {
		SpringApplication.run(CfmotoEurekaApplication.class, args);
	}
}
