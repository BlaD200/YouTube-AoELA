package com.vsynytsyn.videoreceiver.service;

import org.springframework.stereotype.Service;


@Service
// https://github.com/saravanastar/video-streaming
public class VideoStreamService {

//    private final VideoRepository videoRepository;
//
//
//    public VideoStreamService(VideoRepository videoRepository) {
//        this.videoRepository = videoRepository;
//    }
//
//    private final Logger logger = LoggerFactory.getLogger(this.getClass());
//
//    /**
//     * Prepare the content.
//     *
//     * @param videoHash String.
//     * @param resolutionHeight String.
//     * @param range    String.
//     * @return ResponseEntity.
//     */
//    public ResponseEntity<byte[]> prepareContent(String videoHash, String resolutionHeight, String range) {
//        long rangeStart = 0;
//        long rangeEnd;
//        byte[] data;
//        Long fileSize;
//
//        Optional<VideoEntity> videoEntityOptional =
//                videoRepository.findByHashEqualsAndResolutionHeightEquals(videoHash, resolutionHeight);
//        if (!videoEntityOptional.isPresent())
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//        VideoEntity videoEntity = videoEntityOptional.get();
//
//        String fileName = videoEntity.getName();
//
//        try {
//            fileSize = getFileSize(fileName);
//            if (range == null) {
//                return ResponseEntity.status(HttpStatus.OK)
//                        .header(CONTENT_TYPE, VIDEO_CONTENT + fileType)
//                        .header(CONTENT_LENGTH, String.valueOf(fileSize))
//                        .body(readByteRange(fullFileName, rangeStart, fileSize - 1)); // Read the object and convert it as bytes
//            }
//            String[] ranges = range.split("-");
//            rangeStart = Long.parseLong(ranges[0].substring(6));
//            if (ranges.length > 1) {
//                rangeEnd = Long.parseLong(ranges[1]);
//            } else {
//                rangeEnd = fileSize - 1;
//            }
//            if (fileSize < rangeEnd) {
//                rangeEnd = fileSize - 1;
//            }
//            data = readByteRange(fullFileName, rangeStart, rangeEnd);
//        } catch (IOException e) {
//            logger.error("Exception while reading the file {}", e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//        String contentLength = String.valueOf((rangeEnd - rangeStart) + 1);
//        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
//                .header(CONTENT_TYPE, VIDEO_CONTENT + fileType)
//                .header(ACCEPT_RANGES, BYTES)
//                .header(CONTENT_LENGTH, contentLength)
//                .header(CONTENT_RANGE, BYTES + " " + rangeStart + "-" + rangeEnd + "/" + fileSize)
//                .body(data);
//
//
//    }
//
//    /**
//     * ready file byte by byte.
//     *
//     * @param filename String.
//     * @param start    long.
//     * @param end      long.
//     * @return byte array.
//     * @throws IOException exception.
//     */
//    public byte[] readByteRange(String filename, long start, long end) throws IOException {
//        Path path = Paths.get("C:\\Users\\Vladyslav Synytsyn\\OneDrive\\Documents\\IdeaProjects\\video-streaming\\src" +
//                "\\main\\resources\\video", filename);
//        try (InputStream inputStream = (Files.newInputStream(path));
//             ByteArrayOutputStream bufferedOutputStream = new ByteArrayOutputStream()) {
//            byte[] data = new byte[BYTE_RANGE];
//            int nRead;
//            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
//                bufferedOutputStream.write(data, 0, nRead);
//            }
//            bufferedOutputStream.flush();
//            byte[] result = new byte[(int) (end - start) + 1];
//            System.arraycopy(bufferedOutputStream.toByteArray(), (int) start, result, 0, result.length);
//            return result;
//        }
//    }
//
//    /**
//     * Get the filePath.
//     *
//     * @return String.
//     */
//    private String getFilePath() {
//        URL url = this.getClass().getResource(VIDEO);
//        return new File(url.getFile()).getAbsolutePath();
//    }
//
//    /**
//     * Content length.
//     *
//     * @param fileName String.
//     * @return Long.
//     */
//    public Long getFileSize(String fileName) {
//        return Optional.ofNullable(fileName)
//                .map(file -> Paths.get("C:\\Users\\Vladyslav Synytsyn\\OneDrive\\Documents\\IdeaProjects\\video-streaming\\src\\main\\resources\\video", file))
//                .map(this::sizeFromFile)
//                .orElse(0L);
//    }
//
//    /**
//     * Getting the size from the path.
//     *
//     * @param path Path.
//     * @return Long.
//     */
//    private Long sizeFromFile(Path path) {
//        try {
//            return Files.size(path);
//        } catch (IOException ioException) {
//            logger.error("Error while getting the file size", ioException);
//        }
//        return 0L;
//    }
}
