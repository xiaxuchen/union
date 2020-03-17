package com.originit.union.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.originit.union.entity.enums.QueueEnum;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xxc、
 */
@Configuration
public class RabbitConfig {

    @Bean
    public DirectExchange messageExchange () {
        return (DirectExchange)ExchangeBuilder.directExchange(QueueEnum.QUEUE_MESSAGE_HANDLE.getExchange())
                .durable(true)
                .build();
    }

    @Bean
    public Queue messageQueue() {
        return new Queue(QueueEnum.QUEUE_MESSAGE_HANDLE.getName());
    }

    @Bean
    public Binding messageBinding (DirectExchange messageExchange,Queue messageQueue) {
        return BindingBuilder
                .bind(messageQueue)
                .to(messageExchange)
                .with(QueueEnum.QUEUE_MESSAGE_HANDLE.getRouteKey());
    }

    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory, Jackson2JsonMessageConverter producerJackson2MessageConverter) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(producerJackson2MessageConverter);
        return rabbitTemplate;
    }

    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter(ObjectMapper objectMapper) {
        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter(objectMapper);
        DefaultJackson2JavaTypeMapper javaTypeMapper = new DefaultJackson2JavaTypeMapper();
        javaTypeMapper.setTrustedPackages("*");
        jackson2JsonMessageConverter.setJavaTypeMapper(javaTypeMapper);
        return jackson2JsonMessageConverter;
    }

}
