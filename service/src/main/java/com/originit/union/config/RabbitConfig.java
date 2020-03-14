package com.originit.union.config;

import com.originit.union.entity.enums.QueueEnum;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public Exchange messageExchange () {
        return ExchangeBuilder.directExchange(QueueEnum.QUEUE_MESSAGE_HANDLE.getExchange())
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
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
