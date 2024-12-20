package com.vsynytsyn.commons.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoToProcessDTO implements Serializable {
    private String pathToFile;
    private String username;
    private String hashValue;
}
