package com.vsynytsyn.videoconverter.service;

import com.vsynytsyn.youtube.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class SourceVideoDeleteService {

    private final int processorsCount = VideoProcessingService.VideoResolutions.values().length;
    private final ConcurrentHashMap<String, Integer> videoProcessingCount;
    private final RabbitTemplate rabbitTemplate;


    public SourceVideoDeleteService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        videoProcessingCount = new ConcurrentHashMap<>();
    }


    public void recordVideoProcessed(String pathToVideo) {
        int processedCount = videoProcessingCount.getOrDefault(pathToVideo, 0) + 1;
        if (processedCount == processorsCount) {
            addVideoToDeletionQueue(pathToVideo);
            videoProcessingCount.remove(pathToVideo);
        } else
            videoProcessingCount.put(pathToVideo, processedCount);
    }


    private void addVideoToDeletionQueue(String pathToVideo) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.DELETION_EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEYS.DeleteVideo.routingKey,
                pathToVideo
        );
    }
}
