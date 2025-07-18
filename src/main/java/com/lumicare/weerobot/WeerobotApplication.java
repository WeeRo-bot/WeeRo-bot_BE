package com.lumicare.weerobot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class WeerobotApplication {

	public static void main(String[] args) {
		SpringApplication.run(WeerobotApplication.class, args);
	}

}
