package com.vsynytsyn.videoreceiver.repository;

import com.vsynytsyn.videoreceiver.domain.VideoThumbnailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoThumbnailRepository extends JpaRepository<VideoThumbnailEntity, String> {

}
