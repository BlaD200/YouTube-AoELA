package com.vsynytsyn.videoreceiver.controller;

import com.vsynytsyn.videoreceiver.domain.VideoEntity;
import com.vsynytsyn.videoreceiver.service.VideoStorageService;
import com.vsynytsyn.videoreceiver.service.VideoStreamService;
import com.vsynytsyn.youtube.responce.ErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/video")
public class VideoController {

    private final List<String> contentTypes = Arrays.asList("video/mp4", "video/webm", "video/ogg");
    private final VideoStorageService storageService;
    private final VideoStreamService videoStreamService;


    @Autowired
    public VideoController(VideoStorageService storageService, VideoStreamService videoStreamService) {
        this.storageService = storageService;
        this.videoStreamService = videoStreamService;
    }


    @GetMapping
    ResponseEntity<Page<VideoEntity>> getAll(
            Pageable pageable, @RequestParam(required = false) String videoName
    ) {
        return ResponseEntity.ok(storageService.getAll(pageable, videoName));
    }

    @GetMapping("/{videoHash}/{resolution}")
    ResponseEntity<VideoEntity> getOne(
            @PathVariable("videoHash") String videoHash,
            @PathVariable("resolution") String resolution
    ) {
        return ResponseEntity.ok(storageService.getOne(videoHash, resolution));
    }


    @PostMapping(value = "/upload", consumes = {"multipart/form-data"})
    ResponseEntity<Object> uploadVideo(
            @RequestParam(name = "filename") String fileName,
            @RequestParam(name = "file") MultipartFile videoFile,
            @RequestParam(name = "username") String username,
            @RequestParam(required = false) String description
    ) {
        if (!contentTypes.contains(videoFile.getContentType()))
            return new ErrorResponse(
                    "Supported media types: " + contentTypes,
                    HttpStatus.UNSUPPORTED_MEDIA_TYPE).getResponseEntity();
        try {
            storageService.store(videoFile, fileName, username, description);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            return new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST).getResponseEntity();
        }
    }


    @GetMapping("/stream")
    public ResponseEntity<byte[]> streamVideo(
            @RequestHeader(value = "Range", required = false) String httpRangeList,
            @RequestParam String videoHash,
            @RequestParam String resolutionHeight
    ) {
        return videoStreamService.prepareContent(videoHash, resolutionHeight, httpRangeList);
    }


    //    @GetMapping("/")
    //    public String listUploadedFiles(Model model) throws IOException {
    //
    //        model.addAttribute("files", storageService.loadAll().map(
    //                path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
    //                        "serveFile", path.getFileName().toString()).build().toUri().toString())
    //                .collect(Collectors.toList()));
    //
    //        return "uploadForm";
    //    }
    //
    //
//    @GetMapping("/files/{filename:.+}")
//    @ResponseBody
//    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
//
//        Resource file = storageService.loadAsResource(filename);
//        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
//                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
//    }
    //
    //
    //    @ExceptionHandler(StorageFileNotFoundException.class)
    //    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
    //        return ResponseEntity.notFound().build();
    //    }
}
