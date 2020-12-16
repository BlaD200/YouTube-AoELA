package com.vsynytsyn.videoreceiver.controller;

import com.vsynytsyn.videoreceiver.service.VideoStorageService;
import com.vsynytsyn.youtube.responce.ErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/video")
public class VideoUploadController {

    private final List<String> contentTypes = Arrays.asList("video/mp4", "video/webm", "video/ogg");
    private final VideoStorageService storageService;


    @Autowired
    public VideoUploadController(VideoStorageService storageService) {
        this.storageService = storageService;
    }


    @PostMapping(value = "/upload", consumes = {"multipart/form-data"})
    ResponseEntity<Object> uploadVideo(
            @RequestParam(name = "filename") String fileName,
            @RequestParam(name = "file") MultipartFile videoFile,
            @RequestParam(name = "username") String username
    ) {
        if (!contentTypes.contains(videoFile.getContentType()))
            return new ErrorResponse(
                    "Supported media types: " + contentTypes,
                    HttpStatus.UNSUPPORTED_MEDIA_TYPE).getResponseEntity();
        try {
            storageService.store(videoFile, fileName, username);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            return new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST).getResponseEntity();
        }
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
