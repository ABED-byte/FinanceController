package com.finance.tracker.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI financeTrackerOpenApi() {
		return new OpenAPI()
				.info(new Info()
						.title("Finance Tracker API")
						.description("Personal finance tracker backend — JWT secured REST API.")
						.contact(new Contact()
								.email("abedshaikh988@gmail.com")
								.name("Abed Shaikh")
								.url("https://monkeytype.com")))
				.servers(List.of(
						new Server().url("http://localhost:8080").description("Local HTTP"),
						new Server().url("https://localhost:8080").description("Local HTTPS (if configured)")))
				.tags(List.of(
						new Tag().name("Auth").description("Register and login"),
						new Tag().name("Transactions").description("Income and expenses"),
						new Tag().name("Categories").description("Budget categories"),
						new Tag().name("Savings goals").description("Monthly savings targets"),
						new Tag().name("Dashboard").description("Summary and trends"),
						new Tag().name("Users").description("Admin user management")))
				.addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
				.components(new Components().addSecuritySchemes("bearerAuth",
						new SecurityScheme()
								.type(SecurityScheme.Type.HTTP)
								.scheme("bearer")
								.bearerFormat("JWT")
								.in(SecurityScheme.In.HEADER)
								.name("Authorization")));
	}
}
