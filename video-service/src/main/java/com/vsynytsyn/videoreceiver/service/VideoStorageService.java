package com.vsynytsyn.videoreceiver.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vsynytsyn.commons.config.RabbitMQConfig;
import com.vsynytsyn.commons.dto.VideoToProcessDTO;
import com.vsynytsyn.videoreceiver.domain.VideoMetadataEntity;
import com.vsynytsyn.videoreceiver.domain.VideoResolutionEntity;
import com.vsynytsyn.videoreceiver.domain.VideoThumbnailEntity;
import com.vsynytsyn.videoreceiver.repository.VideoMetadataRepository;
import com.vsynytsyn.videoreceiver.repository.VideoResolutionRepository;
import com.vsynytsyn.videoreceiver.repository.VideoThumbnailRepository;
import lombok.SneakyThrows;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
//@ConfigurationProperties(prefix = "storage-service")
public class VideoStorageService {

    private final SimpleDateFormat sdf;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final VideoMetadataRepository videoMetadataRepository;
    private final VideoResolutionRepository videoResolutionRepository;
    private final VideoThumbnailRepository videoThumbnailRepository;
    @Value("${storage-service.base-store-path}")
    public String BASE_STORE_PATH;


    @Autowired
    public VideoStorageService(SimpleDateFormat sdf, RabbitTemplate rabbitTemplate,
                               ObjectMapper objectMapper,
                               VideoMetadataRepository videoMetadataRepository,
                               VideoResolutionRepository videoResolutionRepository,
                               VideoThumbnailRepository videoThumbnailRepository) {
        this.sdf = sdf;
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
        this.videoMetadataRepository = videoMetadataRepository;
        this.videoResolutionRepository = videoResolutionRepository;
        this.videoThumbnailRepository = videoThumbnailRepository;
    }


    public Page<VideoMetadataEntity> getAll(Pageable pageable, String videoName) {

        if (videoName != null) {
            return videoMetadataRepository.findAllByTitleContainsIgnoreCase(pageable, videoName);
        } else {
            return videoMetadataRepository.findAll(pageable);
        }
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
        VideoMetadataEntity videoMetadataEntity = VideoMetadataEntity
                .builder()
                .hash(md5Hex)
                .title(fileName)
                .authorUsername(username)
                .description(description)
                .build();
        videoMetadataRepository.save(videoMetadataEntity);

        sendMessages(filePath.toString(), username, md5Hex);
    }


    public VideoMetadataEntity getOne(String videoHash, String resolution) {
        Optional<VideoResolutionEntity> videoResolutionEntity =
                videoResolutionRepository.findByIdHashEqualsAndIdResolutionHeightEquals(videoHash, resolution);
        return videoResolutionEntity.map(VideoResolutionEntity::getMetadata).orElse(null);
    }


    @Transactional
    public byte[] getVideoThumbnail(final String videoHash) throws IOException {
        Optional<VideoMetadataEntity> videoMetadataOptional = videoMetadataRepository.findByHashEquals(videoHash);
        Optional<VideoThumbnailEntity> thumbnailOptional = videoThumbnailRepository.findById(videoHash);
        if (!(thumbnailOptional.isPresent() && videoMetadataOptional.isPresent())) {
            throw new IllegalArgumentException("Thumbnail not exists");
        }
        VideoMetadataEntity videoMetadataEntity = videoMetadataOptional.get();

        String filename = String.format("%s.png", videoHash);
        String username = videoMetadataEntity.getAuthorUsername();
        Path path = Paths.get(BASE_STORE_PATH, username, "thumbnails", filename);

        return Files.readAllBytes(path);
    }


    @Transactional
    public List<String> getVideoResolutions(final String videoHash) {
        List<VideoResolutionEntity> videoResolutionEntities = videoResolutionRepository.findAllByIdHashEquals(videoHash);
        if (videoResolutionEntities.isEmpty() || !videoMetadataRepository.existsById(videoHash)) {
            throw new IllegalArgumentException("Resolutions not exist");
        }

        return videoResolutionEntities.stream()
                .map(r -> r.getId().getResolutionHeight())
                .sorted((o1, o2) -> Integer.valueOf(o1).compareTo(Integer.parseInt(o2)))
                .collect(Collectors.toList());
    }

    @SneakyThrows
    private void sendMessages(String pathToFile, String username, String hashValue) {
        VideoToProcessDTO messageBody =
                VideoToProcessDTO
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
