package com.megthink.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.megthink.gateway.model.FileStorage;

@SpringBootApplication
@EnableConfigurationProperties({ FileStorage.class })
public class MNPApplication {

	public static void main(String[] args) {
		SpringApplication.run(MNPApplication.class, args);
	}

}
