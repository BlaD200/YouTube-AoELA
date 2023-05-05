package com.vsynytsyn.videoreceiver.domain;

import lombok.*;

import javax.persistence.*;

@Data
@EqualsAndHashCode(of = "hash")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "video_thumbnail")
public class VideoThumbnailEntity {

    @Id
    @Column
    private String hash;

    @MapsId
    @OneToOne
    @JoinColumn(name = "hash")
    private VideoMetadataEntity videoMetadata;
}
