package com.vsynytsyn.videoreceiver.service;

import com.sun.javaws.exceptions.InvalidArgumentException;
import com.vsynytsyn.videoreceiver.domain.VideoEntity;
import com.vsynytsyn.videoreceiver.repository.VideoRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class VideoHashService {

    private final VideoRepository videoRepository;


    public VideoHashService(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }


    @Transactional
    public String saveProcessedVideo(String originalVideoHash, String resolutionHeight)
            throws InvalidArgumentException {
        Optional<VideoEntity> originalVideoEntityOptional =
                videoRepository.findByHashEqualsAndResolutionHeightEquals(originalVideoHash, "");
        if (!originalVideoEntityOptional.isPresent())
            throw new InvalidArgumentException(new String[]{"originalVideoHash"});
        VideoEntity originalVideoEntity = originalVideoEntityOptional.get();

        String newHash = DigestUtils.md5Hex(originalVideoHash);
        VideoEntity processedVideo = VideoEntity
                .builder()
                .name(originalVideoEntity.getName())
                .description(originalVideoEntity.getDescription())
                .authorUsername(originalVideoEntity.getAuthorUsername())
                .hash(newHash)
                .resolutionHeight(resolutionHeight)
                .build();

        videoRepository.save(processedVideo);

        return newHash;
    }

    @Transactional
    public void deleteOriginalVideoFromDB(String originalVideoHash){
        videoRepository.deleteByHashEqualsAndResolutionHeightEquals(originalVideoHash, "");
    }
}


