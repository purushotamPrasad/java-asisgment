package com.qssence.backend.document_initiation_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.qssence.backend.document_initiation_service.client")
public class DocumentInitiationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DocumentInitiationServiceApplication.class, args);
	}

}
