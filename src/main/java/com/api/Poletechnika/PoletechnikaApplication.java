package com.api.Poletechnika;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class PoletechnikaApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(PoletechnikaApplication.class, args);
	}

}
