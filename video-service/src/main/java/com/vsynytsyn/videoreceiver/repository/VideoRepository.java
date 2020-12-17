package com.vsynytsyn.videoreceiver.repository;

import com.vsynytsyn.videoreceiver.domain.VideoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VideoRepository extends JpaRepository<VideoEntity, Long> {

    Optional<VideoEntity> findByHashEqualsAndResolutionHeightEquals(String hash, String resolutionHeight);
    Page<VideoEntity> findAllByNameContainsIgnoreCase(Pageable pageable, String videoName);
    Page<VideoEntity> findAllByResolutionHeightNot(Pageable pageable, String empty);

    void deleteByHashEqualsAndResolutionHeightEquals(String hash, String resolutionHeight);
}
