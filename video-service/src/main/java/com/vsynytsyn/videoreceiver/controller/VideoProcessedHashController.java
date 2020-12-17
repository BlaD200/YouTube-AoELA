package com.vsynytsyn.videoreceiver.controller;

import com.sun.javaws.exceptions.InvalidArgumentException;
import com.vsynytsyn.videoreceiver.service.VideoHashService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/video/processed")
public class VideoProcessedHashController {

    private final VideoHashService videoHashService;


    public VideoProcessedHashController(VideoHashService videoHashService) {
        this.videoHashService = videoHashService;
    }


    @GetMapping
    public ResponseEntity<Object> createNewHashForProcessedVideo(
            @RequestParam String originalVideoHash,
            @RequestParam String resolutionHeight
    ){
        try {
            String newHash = videoHashService.saveProcessedVideo(originalVideoHash, resolutionHeight);
            return ResponseEntity.ok(newHash);
        } catch (InvalidArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("/deleteOriginal")
    public void createNewHashForProcessedVideo(
            @RequestParam String originalVideoHash
    ){
        videoHashService.deleteOriginalVideoFromDB(originalVideoHash);
    }


}
