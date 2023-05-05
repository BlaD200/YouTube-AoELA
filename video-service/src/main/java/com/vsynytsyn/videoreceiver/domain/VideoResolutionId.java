package com.vsynytsyn.videoreceiver.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class VideoResolutionId implements Serializable {

    @Column(name = "hash")
    private String hash;
    @Column(name = "resolution_height")
    private String resolutionHeight;
}
