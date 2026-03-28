package com.firstlogistics.deliverservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class DeliverServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DeliverServiceApplication.class, args);
	}

}
