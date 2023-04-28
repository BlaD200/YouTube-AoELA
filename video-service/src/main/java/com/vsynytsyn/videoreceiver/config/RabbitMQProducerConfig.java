package com.vsynytsyn.videoreceiver.config;

import com.vsynytsyn.commons.config.ModelMapperConfiguration;
import com.vsynytsyn.commons.config.RabbitMQConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({RabbitMQConfig.class, ModelMapperConfiguration.class})
public class RabbitMQProducerConfig {

}
