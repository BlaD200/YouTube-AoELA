package com.vsynytsyn.videoreceiver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@Builder
//@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "hash")
public class VideoDTO {
    private String hash;
    private String title;
    private String description;
    private String authorUsername;

//    public VideoDTO(String hash, String title, String description, String authorUsername) {
//        this.hash = hash;
//        this.title = title;
//        this.description = description;
//        this.authorUsername = authorUsername;
//    }

    private List<VideoResolutionDTO> resolutions;
}
