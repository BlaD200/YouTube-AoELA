package com.vsynytsyn.videoconverter.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vsynytsyn.youtube.dto.RabbitMessageDTO;
import lombok.SneakyThrows;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@EnableRabbit
public class RabbitListeners {
    
    private final ObjectMapper objectMapper;


    @Autowired
    public RabbitListeners(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    @SneakyThrows
    @RabbitListener(queues = "Video720ProcessingQueue")
    public void processVideo720(byte[] message){
        RabbitMessageDTO messageDTO = objectMapper.readValue(message, RabbitMessageDTO.class);

        System.out.printf("[720] %s | %s\n", messageDTO.getUsername(), messageDTO.getStoreFilename());
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("END [720] %s | %s\n", messageDTO.getUsername(), messageDTO.getStoreFilename());
    }


    @SneakyThrows
    @RabbitListener(queues = "Video240ProcessingQueue")
    public void processVideo240(byte[] message){
        RabbitMessageDTO messageDTO = objectMapper.readValue(message, RabbitMessageDTO.class);

        System.out.printf("[240] %s | %s\n", messageDTO.getUsername(), messageDTO.getStoreFilename());
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("END [240] %s | %s\n", messageDTO.getUsername(), messageDTO.getStoreFilename());
    }


    @SneakyThrows
    @RabbitListener(queues = "Video360ProcessingQueue")
    public void processVideo360(byte[] message){
        RabbitMessageDTO messageDTO = objectMapper.readValue(message, RabbitMessageDTO.class);

        System.out.printf("[360] %s | %s\n", messageDTO.getUsername(), messageDTO.getStoreFilename());
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("END [360] %s | %s\n", messageDTO.getUsername(), messageDTO.getStoreFilename());
    }


    @SneakyThrows
    @RabbitListener(queues = "Video1080ProcessingQueue")
    public void processVideo1080(byte[] message){
        RabbitMessageDTO messageDTO = objectMapper.readValue(message, RabbitMessageDTO.class);

        System.out.printf("[1080] %s | %s\n", messageDTO.getUsername(), messageDTO.getStoreFilename());
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("END [1080] %s | %s\n", messageDTO.getUsername(), messageDTO.getStoreFilename());
    }
}
