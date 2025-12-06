package com.qssence.backend.subscription_panel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class SubscriptionPanelApplication {

	public static void main(String[] args) {
		SpringApplication.run(SubscriptionPanelApplication.class, args);
	}
}
