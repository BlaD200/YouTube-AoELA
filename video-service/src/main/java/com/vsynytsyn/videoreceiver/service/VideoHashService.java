package com.vsynytsyn.videoreceiver.service;

import com.vsynytsyn.videoreceiver.domain.VideoMetadataEntity;
import com.vsynytsyn.videoreceiver.domain.VideoResolutionEntity;
import com.vsynytsyn.videoreceiver.domain.VideoResolutionId;
import com.vsynytsyn.videoreceiver.domain.VideoThumbnailEntity;
import com.vsynytsyn.videoreceiver.repository.VideoMetadataRepository;
import com.vsynytsyn.videoreceiver.repository.VideoResolutionRepository;
import com.vsynytsyn.videoreceiver.repository.VideoThumbnailRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class VideoHashService {

    private final VideoMetadataRepository videoMetadataRepository;
    private final VideoResolutionRepository videoResolutionRepository;
    private final VideoThumbnailRepository videoThumbnailRepository;


    public VideoHashService(VideoMetadataRepository videoMetadataRepository,
                            VideoResolutionRepository videoResolutionRepository,
                            VideoThumbnailRepository videoThumbnailRepository) {
        this.videoMetadataRepository = videoMetadataRepository;
        this.videoResolutionRepository = videoResolutionRepository;
        this.videoThumbnailRepository = videoThumbnailRepository;
    }


    @Transactional
    public String saveProcessedVideo(String originalVideoHash, String resolutionHeight)
            throws IllegalArgumentException {
        Optional<VideoMetadataEntity> originalVideoMetadataOptional =
                videoMetadataRepository.findByHashEquals(originalVideoHash);
        if (!originalVideoMetadataOptional.isPresent())
            throw new IllegalArgumentException("originalVideoHash");

//        String newHash = DigestUtils.md5Hex(originalVideoHash);
        VideoResolutionId videoResolutionId = new VideoResolutionId();
        videoResolutionId.setHash(originalVideoHash);
        videoResolutionId.setResolutionHeight(resolutionHeight);
        VideoResolutionEntity processedVideo = VideoResolutionEntity
                .builder()
                .id(videoResolutionId)
                .metadata(originalVideoMetadataOptional.get())
                .build();

        videoResolutionRepository.save(processedVideo);

        return originalVideoHash;
    }

    @Transactional
    public String saveProcessedThumbnail(String originalVideoHash)
            throws IllegalArgumentException {
        Optional<VideoMetadataEntity> originalVideoMetadataOptional =
                videoMetadataRepository.findByHashEquals(originalVideoHash);
        if (!originalVideoMetadataOptional.isPresent())
            throw new IllegalArgumentException("originalVideoHash");

        VideoThumbnailEntity processedThumbnail = VideoThumbnailEntity
                .builder()
                .hash(originalVideoHash)
                .videoMetadata(originalVideoMetadataOptional.get())
                .build();

        videoThumbnailRepository.save(processedThumbnail);

        return originalVideoHash;
    }

//    @Transactional
//    public void deleteOriginalVideoFromDB(String originalVideoHash){
//        videoMetadataRepository.deleteByHashEquals(originalVideoHash);
//    }
}


