package com.vsynytsyn.videoreceiver.repository;

import com.vsynytsyn.videoreceiver.domain.VideoMetadataEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VideoMetadataRepository extends JpaRepository<VideoMetadataEntity, String> {

    Optional<VideoMetadataEntity> findByHashEquals(String hash);

    Page<VideoMetadataEntity> findAllByTitleContainsIgnoreCase(Pageable pageable, String videoName);
//    Page<VideoMetadataEntity> findAllByResolutionHeightNot(Pageable pageable, String empty);

//    @Query(value =
//            "SELECT vm FROM VideoMetadataEntity vm " +
//                    "JOIN VideoResolutionEntity vr ON vm.hash=vr.id.hash")
//    @Override
//    Page<VideoDTO> findAll(Pageable pageable);

//    @Query("SELECT v.hash, v.author, v.title, v.description, r.resolution " +
//            "FROM VideoMetadataEntity v " +
//            "JOIN v.resolutions r")
//    VideoDTO findByHashWithResolutions(@Param("hash") String hash);


    void deleteByHashEquals(String hash);
}
