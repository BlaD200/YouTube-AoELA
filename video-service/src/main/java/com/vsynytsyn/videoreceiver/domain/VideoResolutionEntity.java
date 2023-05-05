package com.vsynytsyn.videoreceiver.domain;

import lombok.*;

import javax.persistence.*;

@Data
@EqualsAndHashCode(of = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "video_resolution")
public class VideoResolutionEntity {

    @EmbeddedId
    private VideoResolutionId id;

    @MapsId("hash")
    @ManyToOne
    @JoinColumn(name = "hash", referencedColumnName = "hash")
    private VideoMetadataEntity metadata;
}
