package com.vsynytsyn.videoreceiver.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vsynytsyn.youtube.config.RabbitMQConfig;
import com.vsynytsyn.youtube.dto.RabbitMessageDTO;
import lombok.SneakyThrows;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Objects;

@Service
//@ConfigurationProperties(prefix = "storage-service")
public class VideoStorageService {

    @Value("${storage-service.base-store-path}")
    public String BASE_STORE_PATH;

    private final SimpleDateFormat sdf;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;


    @Autowired
    public VideoStorageService(SimpleDateFormat sdf, RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.sdf = sdf;
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }


    public void store(MultipartFile videoFile, String fileName, String username) throws IOException {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String[] contents = Objects.requireNonNull(videoFile.getContentType()).split("/");
        String storeFileName = String.format("%s.(%s).%s", fileName, sdf.format(timestamp),
                contents[contents.length - 1]);

        Path filePath = Paths.get(BASE_STORE_PATH, username, "unconverted", storeFileName);
        if (Files.notExists(filePath.getParent()))
            Files.createDirectories(filePath.getParent());

        Files.write(filePath, videoFile.getBytes());

        sendMessages(filePath.toString(), username, fileName);
    }


    @SneakyThrows
    private void sendMessages(String pathToFile, String username, String originalFilename) {
        RabbitMessageDTO messageBody =
                RabbitMessageDTO
                        .builder()
                        .originalFilename(originalFilename)
                        .pathToFile(pathToFile)
                        .username(username)
                        .build();
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        objectMapper.writeValue(arrayOutputStream, messageBody);

        for (RabbitMQConfig.ROUTING_KEYS value : RabbitMQConfig.ROUTING_KEYS.values()) {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_NAME,
                    value.routingKey,
                    arrayOutputStream.toByteArray()
            );
        }
    }

}
