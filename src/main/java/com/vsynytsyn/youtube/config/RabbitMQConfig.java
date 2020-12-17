package com.vsynytsyn.youtube.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"com.vsynytsyn.youtube.config"})
public class RabbitMQConfig {
    public static final String EXCHANGE_NAME = "VideoProcessingExchange";


    @Bean
    public Queue videoProcessingQueue360() {
        return QueueBuilder.durable(QueueNames.Queue360.queueName).build();
    }


    @Bean
    public Queue videoProcessingQueue720() {
        return QueueBuilder.durable(QueueNames.Queue720.queueName).build();
    }


    @Bean
    public Queue videoProcessingQueue240() {
        return QueueBuilder.durable(QueueNames.Queue240.queueName).build();
    }


    @Bean
    public Queue videoProcessingQueue1080() {
        return QueueBuilder.durable(QueueNames.Queue1080.queueName).build();
    }


    @Bean
    public Exchange videoProcessingExchange() {
        return ExchangeBuilder
                .directExchange(EXCHANGE_NAME)
                .durable(true)
                .build();
    }


    @Bean
    public Binding videoProcessingBinding360() {
        return BindingBuilder
                .bind(videoProcessingQueue360())
                .to(videoProcessingExchange())
                .with(ROUTING_KEYS.Video360.routingKey)
                .noargs();
    }


    @Bean
    public Binding videoProcessingBinding720() {
        return BindingBuilder
                .bind(videoProcessingQueue720())
                .to(videoProcessingExchange())
                .with(ROUTING_KEYS.Video720.routingKey)
                .noargs();
    }


    @Bean
    public Binding videoProcessingBinding240() {
        return BindingBuilder
                .bind(videoProcessingQueue240())
                .to(videoProcessingExchange())
                .with(ROUTING_KEYS.Video240.routingKey)
                .noargs();
    }


    @Bean
    public Binding videoProcessingBinding1080() {
        return BindingBuilder
                .bind(videoProcessingQueue1080())
                .to(videoProcessingExchange())
                .with(ROUTING_KEYS.Video1080.routingKey)
                .noargs();
    }


    public enum ROUTING_KEYS {
        Video240("processVideo240"),
        Video360("processVideo360"),
        Video720("processVideo720"),
        Video1080("processVideo1080");

        public final String routingKey;


        ROUTING_KEYS(String routingKey) {
            this.routingKey = routingKey;
        }
    }


    public enum QueueNames {
        Queue240("Video240ProcessingQueue"),
        Queue360("Video360ProcessingQueue"),
        Queue720("Video720ProcessingQueue"),
        Queue1080("Video1080ProcessingQueue");

        public final String queueName;


        QueueNames(String queueName) {
            this.queueName = queueName;
        }
    }
}
