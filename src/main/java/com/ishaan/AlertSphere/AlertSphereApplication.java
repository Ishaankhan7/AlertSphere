package com.ishaan.AlertSphere;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AlertSphereApplication {

	public static void main(String[] args) {
		SpringApplication.run(AlertSphereApplication.class, args);
	}

}
