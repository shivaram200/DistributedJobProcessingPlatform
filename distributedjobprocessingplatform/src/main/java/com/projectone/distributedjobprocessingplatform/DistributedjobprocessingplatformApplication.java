package com.projectone.distributedjobprocessingplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class DistributedjobprocessingplatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(DistributedjobprocessingplatformApplication.class, args);
	}

}
