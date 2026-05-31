package com.ticketti.ms_donaciones.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Debe coincidir EXACTAMENTE con MSCarrito
    public static final String EXCHANGE       = "ticketti.exchange";
    public static final String QUEUE_PAGO     = "pago.aprobado";
    public static final String ROUTING_KEY    = "pago.aprobado";

    @Bean
    public DirectExchange tickettiExchange() {
        // durable=true: sobrevive reinicios de RabbitMQ
        return new DirectExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue queuePagoAprobado() {
        return QueueBuilder.durable(QUEUE_PAGO).build();
    }

    @Bean
    public Binding bindingPago(Queue queuePagoAprobado,
                               DirectExchange tickettiExchange) {
        return BindingBuilder
                .bind(queuePagoAprobado)
                .to(tickettiExchange)
                .with(ROUTING_KEY);
    }

    /**
     * Convierte automáticamente JSON ↔ objetos Java.
     * MSCarrito serializa el CarritoDeCompras como JSON en el outbox.
     */
    @Bean
    @SuppressWarnings("removal")
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    @SuppressWarnings({"null", "removal"})
    public RabbitTemplate rabbitTemplate(ConnectionFactory cf,
                                         Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(cf);
        template.setMessageConverter(messageConverter);
        return template;
    }

    @Bean
    @SuppressWarnings({"null", "removal"})
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter messageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        return factory;
    }
}
