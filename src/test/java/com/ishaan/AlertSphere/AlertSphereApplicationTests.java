package com.ishaan.AlertSphere;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"spring.mongodb.auto-index-creation=false",
		"spring.data.mongodb.auto-index-creation=false",
		"alerts.weather-check.enabled=false",
		"alerts.kafka.consumer.enabled=false",
		"alerts.devassist.consumer.enabled=false"
})
class AlertSphereApplicationTests {

	@Test
	void contextLoads() {
	}

}
