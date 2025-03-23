package com.letzgo.LetzgoBe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class LetzgoBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(LetzgoBeApplication.class, args);
	}

}
