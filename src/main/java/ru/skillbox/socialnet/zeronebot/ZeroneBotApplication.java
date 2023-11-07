package ru.skillbox.socialnet.zeronebot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ZeroneBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZeroneBotApplication.class, args);
	}

}
