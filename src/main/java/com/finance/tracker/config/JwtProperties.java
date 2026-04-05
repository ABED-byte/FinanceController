package com.finance.tracker.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

	private String secret;
	private long expirationMs = 86400000L;
}
