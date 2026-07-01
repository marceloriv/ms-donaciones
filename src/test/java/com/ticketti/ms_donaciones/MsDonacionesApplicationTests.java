package com.ticketti.ms_donaciones;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@SpringBootTest(properties = {
		"spring.config.import=",
		"spring.profiles.active=test",
		"spring.main.allow-bean-definition-overriding=true"
})
class MsDonacionesApplicationTests {

	@Test
	void contextLoads() {
	}

	@TestConfiguration
	static class RabbitTestConfig {
		@Bean
		@Primary
		SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
				ConnectionFactory connectionFactory,
				Jackson2JsonMessageConverter messageConverter) {
			SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
			factory.setConnectionFactory(connectionFactory);
			factory.setMessageConverter(messageConverter);
			factory.setAutoStartup(false);
			return factory;
		}
	}

}
