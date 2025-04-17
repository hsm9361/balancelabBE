package com.ai.balancelab_be;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BalancelabBeApplication {

	public static void main(String[] args) {

		SpringApplication.run(BalancelabBeApplication.class, args);
	}

}
