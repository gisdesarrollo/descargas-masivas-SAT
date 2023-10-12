package com.descarga.cfdi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DescargaCfdiSatApplication {

	public static void main(String[] args) {
		SpringApplication.run(DescargaCfdiSatApplication.class, args);
	}

}
