package com.hms.PharmacyMS;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableCaching
@EnableFeignClients
public class PharmacyMsApplication {

	public static void main(String[] args) {
		SpringApplication.run(PharmacyMsApplication.class, args);
	}
	@Bean
	public CommandLineRunner checkCacheManager(CacheManager cacheManager) {
		return args -> {
			System.out.println("=======================================================");
			System.out.println("LOẠI CACHE ĐANG DÙNG: " + cacheManager.getClass().getName());
			System.out.println("=======================================================");
		};
	}
}
