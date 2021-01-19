package com.supercompany.subscriptionservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Collections;

@SpringBootApplication
@EnableScheduling
@Configuration
public class SubscriptionServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SubscriptionServiceApplication.class, args);
	}

	private ApiInfo apiInfo() {
		return new ApiInfo(
				"Subscription Service REST API",
				"basic subscription service app",
				"API TOS",
				"Terms of service",
				new Contact("Ali YUCE", "www.example.com", "myeaddress@company.com"),
				"License of API",
				"API license URL",
				Collections.emptyList());
	}

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
				.apiInfo(apiInfo())
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.supercompany.subscriptionservice.controller"))
				.paths(PathSelectors.any())
				.build()
				.useDefaultResponseMessages(false);
	}
}
