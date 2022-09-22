package com.travel.busyflights;


import org.springdoc.core.GroupedOpenApi;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class BusyFlightsApplication {

	public static void main(String[] args) {
		SpringApplication.run(BusyFlightsApplication.class, args);
	}

	//Configuration scan packages to Open API
	@Bean
	public GroupedOpenApi publicApi(){
		return GroupedOpenApi.builder()
				.group("springshop-public")
				.packagesToScan("com.travel" +
						"")
				.build();
	}

}
