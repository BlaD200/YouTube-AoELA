package com.vsynytsyn.videoreceiver.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vsynytsyn.videoreceiver.domain.VideoEntity;
import com.vsynytsyn.videoreceiver.repository.VideoRepository;
import com.vsynytsyn.youtube.config.RabbitMQConfig;
import com.vsynytsyn.youtube.dto.RabbitMessageDTO;
import lombok.SneakyThrows;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final VideoRepository videoRepository;


    @Autowired
    public VideoStorageService(SimpleDateFormat sdf, RabbitTemplate rabbitTemplate,
                               ObjectMapper objectMapper, VideoRepository videoRepository) {
        this.sdf = sdf;
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
        this.videoRepository = videoRepository;
    }


    public Page<VideoEntity> getAll(Pageable pageable, String videoName) {
        if (videoName != null)
            return videoRepository.findAllByNameContainsIgnoreCase(pageable, videoName);
        return videoRepository.findAllByResolutionHeightNot(pageable, "");
    }


    public void store(MultipartFile videoFile, String fileName, String username, String description) throws IOException {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String[] contents = Objects.requireNonNull(videoFile.getContentType()).split("/");
        String videoFormat = contents[contents.length - 1];
        String storeFileName = String.format("%s.(%s).%s", fileName, sdf.format(timestamp),
                videoFormat);

        Path filePath = Paths.get(BASE_STORE_PATH, username, "unconverted", storeFileName);
        if (Files.notExists(filePath.getParent()))
            Files.createDirectories(filePath.getParent());
        Files.write(filePath, videoFile.getBytes());

        String md5Hex = DigestUtils
                .md5Hex(storeFileName);
        VideoEntity videoEntity = VideoEntity
                .builder()
                .name(fileName)
                .authorUsername(username)
                .description(description)
                .hash(md5Hex)
                .resolutionHeight("")
                .build();
        videoRepository.save(videoEntity);

        sendMessages(filePath.toString(), username, md5Hex);
    }


    @SneakyThrows
    private void sendMessages(String pathToFile, String username, String hashValue) {
        RabbitMessageDTO messageBody =
                RabbitMessageDTO
                        .builder()
                        .hashValue(hashValue)
                        .pathToFile(pathToFile)
                        .username(username)
                        .build();
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        objectMapper.writeValue(arrayOutputStream, messageBody);

        for (RabbitMQConfig.ROUTING_KEYS value : RabbitMQConfig.ROUTING_KEYS.values()) {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.PROCESSING_EXCHANGE_NAME,
                    value.routingKey,
                    arrayOutputStream.toByteArray()
            );
        }
    }

}
