package com.vsynytsyn.videoconverter.config;

import com.vsynytsyn.youtube.config.ModelMapperConfiguration;
import com.vsynytsyn.youtube.config.RabbitMQConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({RabbitMQConfig.class, ModelMapperConfiguration.class})
public class RabbitMQConsumerConfig {
}
