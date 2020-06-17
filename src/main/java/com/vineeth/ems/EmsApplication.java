package com.vineeth.ems;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableLoadTimeWeaving;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableAdminServer
@EnableScheduling
@EnableTransactionManagement(mode = AdviceMode.ASPECTJ)
public class EmsApplication {
	@Value("${payroll.resttemplate.readtimeout}")
	private int readTimeout;

	@Value("${payroll.resttemplate.connecttimeout}")
	private int connectTimeout;

	public static void main(String[] args) {
		SpringApplication.run(EmsApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		builder.requestFactory(this::getClientHttpRequestFactory);
		return builder.build();
	}

	private ClientHttpRequestFactory getClientHttpRequestFactory() {
		HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
		clientHttpRequestFactory.setConnectTimeout(connectTimeout);
		clientHttpRequestFactory.setReadTimeout(readTimeout);
		return clientHttpRequestFactory;
	}
}
