package com.vsynytsyn.videoreceiver.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Objects;

@Service
public class VideoStorageService {

    public static final String BASE_STORE_PATH = "C:\\Users\\Vladyslav Synytsyn\\Videos\\YouTube";
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSS");


    public void store(MultipartFile videoFile, String fileName, String username) throws IOException {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String[] contents = Objects.requireNonNull(videoFile.getContentType()).split("/");
        String storeFileName = String.format("%s.(%s).%s", fileName, sdf.format(timestamp),
                contents[contents.length - 1]);

        Path filePath = Paths.get(BASE_STORE_PATH, username, "unconverted", storeFileName);
        if (Files.notExists(filePath.getParent()))
            Files.createDirectories(filePath.getParent());

        Files.write(filePath, videoFile.getBytes());
    }

}
