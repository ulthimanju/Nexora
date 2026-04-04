package com.nexora.assessment.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexora.assessment.constants.ServiceConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public TopicExchange assessmentExchange() {
        return ExchangeBuilder
                .topicExchange(ServiceConstants.ASSESSMENT_EXCHANGE)
                .durable(true)
                .build();
    }

    @Bean
    public Queue assessmentCompletedQueue() {
        return QueueBuilder.durable("nexora.assessment.completed.queue").build();
    }

    @Bean
    public Binding assessmentCompletedBinding(Queue assessmentCompletedQueue, TopicExchange assessmentExchange) {
        return BindingBuilder.bind(assessmentCompletedQueue)
                .to(assessmentExchange)
                .with(ServiceConstants.ASSESSMENT_COMPLETED_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }
}
