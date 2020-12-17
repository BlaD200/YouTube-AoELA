package com.vsynytsyn.videoreceiver.service;

import com.vsynytsyn.videoreceiver.domain.VideoEntity;
import com.vsynytsyn.videoreceiver.repository.VideoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;


@Service
// https://github.com/saravanastar/video-streaming
public class VideoStreamService {

    public static final String CONTENT_TYPE = "Content-Type";
    public static final String VIDEO_CONTENT = "video/mp4";
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String CONTENT_RANGE = "Content-Range";
    public static final String ACCEPT_RANGES = "Accept-Ranges";
    public static final String BYTES = "bytes";
    public static final int BYTE_RANGE = 1024;
    private final VideoRepository videoRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Value("${storage-service.base-store-path}")
    public String BASE_STORE_PATH;


    public VideoStreamService(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }


    /**
     * Prepare the content.
     *
     * @param videoHash        String.
     * @param resolutionHeight String.
     * @param range            String.
     * @return ResponseEntity.
     */
    public ResponseEntity<byte[]> prepareContent(String videoHash, String resolutionHeight, String range) {
        long rangeStart = 0;
        long rangeEnd;
        byte[] data;
        Long fileSize;

        Optional<VideoEntity> videoEntityOptional =
                videoRepository.findByHashEqualsAndResolutionHeightEquals(videoHash, resolutionHeight);
        if (!videoEntityOptional.isPresent())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        VideoEntity videoEntity = videoEntityOptional.get();

        String fileName = String.format("[%s]%s.mp4", videoEntity.getResolutionHeight(), videoEntity.getHash());
        String authorUsername = videoEntity.getAuthorUsername();
        String pathToFile = Paths.get(BASE_STORE_PATH, authorUsername, "converted", fileName).toString();

        try {
            fileSize = getFileSize(pathToFile);
            if (range == null) {
                return ResponseEntity.status(HttpStatus.OK)
                        .header(CONTENT_TYPE, VIDEO_CONTENT)
                        .header(CONTENT_LENGTH, String.valueOf(fileSize))
                        .body(readByteRange(pathToFile, rangeStart, fileSize - 1)); // Read the object and convert
                // it as bytes
            }
            String[] ranges = range.split("-");
            rangeStart = Long.parseLong(ranges[0].substring(6));
            if (ranges.length > 1) {
                rangeEnd = Long.parseLong(ranges[1]);
            } else {
                rangeEnd = fileSize - 1;
            }
            if (fileSize < rangeEnd) {
                rangeEnd = fileSize - 1;
            }
            data = readByteRange(pathToFile, rangeStart, rangeEnd);
        } catch (IOException e) {
            logger.error("Exception while reading the file {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        String contentLength = String.valueOf((rangeEnd - rangeStart) + 1);
        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .header(CONTENT_TYPE, VIDEO_CONTENT)
                .header(ACCEPT_RANGES, BYTES)
                .header(CONTENT_LENGTH, contentLength)
                .header(CONTENT_RANGE, BYTES + " " + rangeStart + "-" + rangeEnd + "/" + fileSize)
                .body(data);


    }


    /**
     * ready file byte by byte.
     *
     * @param pathToFile String.
     * @param start    long.
     * @param end      long.
     * @return byte array.
     * @throws IOException exception.
     */
    public byte[] readByteRange(String pathToFile, long start, long end) throws IOException {
        Path path = Paths.get(pathToFile);
        try (InputStream inputStream = (Files.newInputStream(path));
             ByteArrayOutputStream bufferedOutputStream = new ByteArrayOutputStream()) {
            byte[] data = new byte[BYTE_RANGE];
            int nRead;
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                bufferedOutputStream.write(data, 0, nRead);
            }
            bufferedOutputStream.flush();
            byte[] result = new byte[(int) (end - start) + 1];
            System.arraycopy(bufferedOutputStream.toByteArray(), (int) start, result, 0, result.length);
            return result;
        }
    }


    /**
     * Content length.
     *
     *
     * @param pathToFile@return Long.
     */
    public Long getFileSize(String pathToFile) {
        return Optional.ofNullable(pathToFile)
                .map(file -> Paths.get(pathToFile))
                .map(this::sizeFromFile)
                .orElse(0L);
    }


    /**
     * Getting the size from the path.
     *
     * @param path Path.
     * @return Long.
     */
    private Long sizeFromFile(Path path) {
        try {
            return Files.size(path);
        } catch (IOException ioException) {
            logger.error("Error while getting the file size", ioException);
        }
        return 0L;
    }
}
