package com.vsynytsyn.videoconverter.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vsynytsyn.commons.dto.VideoToProcessDTO;
import com.vsynytsyn.videoconverter.service.VideoProcessingService;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@EnableRabbit
public class VideoProcessingListeners {
    
    private final ObjectMapper objectMapper;
    private final VideoProcessingService processingService;


    @Autowired
    public VideoProcessingListeners(ObjectMapper objectMapper, VideoProcessingService processingService) {
        this.objectMapper = objectMapper;
        this.processingService = processingService;
    }


    @RabbitListener(queues = "Video720ProcessingQueue")
    public void processVideo720(byte[] message){
        try {
            VideoToProcessDTO messageDTO = objectMapper.readValue(message, VideoToProcessDTO.class);
            processingService.processVideo(
                    messageDTO.getPathToFile(),
                    messageDTO.getHashValue(),
                    messageDTO.getUsername(),
                    VideoProcessingService.VideoResolutions.R720p
            );
        } catch (IOException e){
            e.printStackTrace();
        }
    }


    @RabbitListener(queues = "Video240ProcessingQueue")
    public void processVideo240(byte[] message){
        try {
            VideoToProcessDTO messageDTO = objectMapper.readValue(message, VideoToProcessDTO.class);
            processingService.processVideo(
                    messageDTO.getPathToFile(),
                    messageDTO.getHashValue(),
                    messageDTO.getUsername(),
                    VideoProcessingService.VideoResolutions.R240p
            );
        } catch (IOException e){
            e.printStackTrace();
        }
    }


    @RabbitListener(queues = "Video360ProcessingQueue")
    public void processVideo360(byte[] message){
        try {
            VideoToProcessDTO messageDTO = objectMapper.readValue(message, VideoToProcessDTO.class);
            processingService.processVideo(
                    messageDTO.getPathToFile(),
                    messageDTO.getHashValue(),
                    messageDTO.getUsername(),
                    VideoProcessingService.VideoResolutions.R360p
            );
        } catch (IOException e){
            e.printStackTrace();
        }
    }


    @RabbitListener(queues = "Video1080ProcessingQueue")
    public void processVideo1080(byte[] message){
        try {
            VideoToProcessDTO messageDTO = objectMapper.readValue(message, VideoToProcessDTO.class);
            processingService.processVideo(
                    messageDTO.getPathToFile(),
                    messageDTO.getHashValue(),
                    messageDTO.getUsername(),
                    VideoProcessingService.VideoResolutions.R1080p
            );
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @RabbitListener(queues = "VideoThumbnailProcessingQueue")
    public void processVideoThumbnail(byte[] message){
        try {
            VideoToProcessDTO messageDTO = objectMapper.readValue(message, VideoToProcessDTO.class);
            processingService.processVideoThumbnail(
                    messageDTO.getPathToFile(),
                    messageDTO.getHashValue(),
                    messageDTO.getUsername()
            );
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
