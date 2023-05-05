package com.vsynytsyn.videoreceiver.domain;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(of = "hash")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "video_metadata")
public class VideoMetadataEntity {

    @Id
    @NotNull
    @Column(nullable = false)
    private String hash;

    @NotNull
    @Column(nullable = false)
    private String title;

    private String description;

    @NotNull
    @Column(name = "author_username", nullable = false)
    private String authorUsername;

}
