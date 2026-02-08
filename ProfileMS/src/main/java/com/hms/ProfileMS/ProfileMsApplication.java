package com.hms.ProfileMS;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ProfileMsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProfileMsApplication.class, args);
	}

}
