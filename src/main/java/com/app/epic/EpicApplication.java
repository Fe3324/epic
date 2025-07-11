package com.app.epic;

import com.app.epic.config.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class EpicApplication {

	public static void main(String[] args) {
		SpringApplication.run(EpicApplication.class, args);
	}

}
