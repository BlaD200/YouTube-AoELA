package com.vsynytsyn.videoreceiver.repository;

import com.vsynytsyn.videoreceiver.domain.VideoResolutionEntity;
import com.vsynytsyn.videoreceiver.domain.VideoResolutionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VideoResolutionRepository extends JpaRepository<VideoResolutionEntity, VideoResolutionId> {

    boolean existsByIdHashEqualsAndIdResolutionHeightEquals(String hash, String resolution);

    List<VideoResolutionEntity> findAllByIdHashEquals(String hash);

    Optional<VideoResolutionEntity> findByIdHashEqualsAndIdResolutionHeightEquals(String hash, String resolution);
}
