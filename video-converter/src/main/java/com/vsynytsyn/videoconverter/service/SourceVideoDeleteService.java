package com.vsynytsyn.videoconverter.service;

import com.vsynytsyn.youtube.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SourceVideoDeleteService {

    private final int processorsCount = VideoProcessingService.VideoResolutions.values().length;
    private final ConcurrentHashMap<String, Integer> videoProcessingCount;
    private final RabbitTemplate rabbitTemplate;
    private final String videoReceiverUrl;


    public SourceVideoDeleteService(RabbitTemplate rabbitTemplate,
                                    @Value("${video-receiver.url}") String videoReceiverUrl) {
        this.rabbitTemplate = rabbitTemplate;
        this.videoReceiverUrl = videoReceiverUrl;
        videoProcessingCount = new ConcurrentHashMap<>();
    }


    public void recordVideoProcessed(String pathToVideo, String originalHash) {
        int processedCount = videoProcessingCount.getOrDefault(pathToVideo, 0) + 1;
        if (processedCount == processorsCount) {
            addVideoToDeletionQueue(pathToVideo, originalHash);
            videoProcessingCount.remove(pathToVideo);
        } else
            videoProcessingCount.put(pathToVideo, processedCount);
    }


    private void addVideoToDeletionQueue(String pathToVideo, String originalHash) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.DELETION_EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEYS.DeleteVideo.routingKey,
                pathToVideo
        );

        try {
            sendDeleteOriginalFileRequest(originalHash);
            System.out.println("Sent delete request for video hash " + originalHash);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void sendDeleteOriginalFileRequest(String originalHash) throws IOException {
        String params = String.format("originalVideoHash=%s", originalHash);
        URL url = new URL(
                String.format("%s/api/video/processed/deleteOriginal?%s", videoReceiverUrl, params)
        );
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setConnectTimeout(3000);
        con.setReadTimeout(3000);

        con.getResponseCode();

        con.disconnect();
    }
}
