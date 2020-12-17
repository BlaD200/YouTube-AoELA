package com.vsynytsyn.youtube.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RabbitMessageDTO implements Serializable {
    private String storeFilename;
    private String username;
}
