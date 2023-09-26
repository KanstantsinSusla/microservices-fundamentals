package com.epam.resourceservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessagingConfig {
    public static final String RESOURCE_QUEUE = "resource.queue";
    public static final String RESOURCE_DLQ = "resource.dlq";

    public static final String RESOURCE_EXCHANGE = "resource_exchange";
    public static final String RESOURCE_EXCHANGE_DLQ = "resource_exchange_dlq";

    public static final String RESOURCE_ROUTING_KEY = "resource_routing_key";
    public static final String RESOURCE_ROUTING_KEY_DLQ = "resource_routing_key_dlq";

    public static final String DLQ_EXCHANGE_ARG = "x-dead-letter-exchange";
    public static final String DLQ_ROUTING_KEY_ARG = "x-dead-letter-routing-key";

    @Bean
    public Queue queue() {
        return QueueBuilder.durable(RESOURCE_QUEUE).withArgument(DLQ_EXCHANGE_ARG, RESOURCE_EXCHANGE_DLQ)
                .withArgument(DLQ_ROUTING_KEY_ARG, RESOURCE_ROUTING_KEY_DLQ).build();
    }

    @Bean Queue dlq() {
        return QueueBuilder.durable(RESOURCE_DLQ).build();
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(RESOURCE_EXCHANGE);
    }

    @Bean
    public TopicExchange exchangeDlq() {
        return new TopicExchange(RESOURCE_EXCHANGE_DLQ);
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(RESOURCE_ROUTING_KEY);
    }

    @Bean
    public Binding bindingDlq(Queue dlq, TopicExchange exchangeDlq) {
        return BindingBuilder.bind(dlq).to(exchangeDlq).with(RESOURCE_ROUTING_KEY_DLQ);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate template(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }
}
