package com.vsynytsyn.videoreceiver.domain;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(of = "videoID")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class VideoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "video_id")
    private Long videoID;

    @NotNull
    @Column(nullable = false)
    private String name;

    private String description;

    @NotNull
    @Column(nullable = false)
    private String authorUsername;

    @NotNull
    @Column(nullable = false)
    private String hash;

    @NotNull
    @Column(nullable = false)
    private String resolutionHeight;

}
