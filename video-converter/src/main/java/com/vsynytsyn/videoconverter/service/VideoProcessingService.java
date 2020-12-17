package com.vsynytsyn.videoconverter.service;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.job.FFmpegJob;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

@Service
public class VideoProcessingService {

    private final String BASE_STORE_PATH;
    private final SimpleDateFormat sdf;

    private final SourceVideoDeleteService deleteService;

    @Value("${ffmpeg-path}")
    private String ffmpegPath;
    @Value("${ffprobe-path}")
    private String ffprobePath;


    @Autowired
    public VideoProcessingService(@Value("${video-output-base-path}") String base_store_path,
                                  SimpleDateFormat sdf, SourceVideoDeleteService deleteService) {
        BASE_STORE_PATH = base_store_path;
        this.sdf = sdf;
        this.deleteService = deleteService;
    }


    public void processVideo(String pathToFile, String originalFilename, String username, VideoResolutions resolution) throws IOException {
        FFmpeg ffmpeg = new FFmpeg(ffmpegPath);
        FFprobe ffprobe = new FFprobe(ffprobePath);

        FFmpegProbeResult probeResult = ffprobe.probe(pathToFile);


        long targetSize = probeResult.getFormat().size / 10;

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String filename = String.format("(%s).%s.(%s).mp4", resolution, originalFilename, sdf.format(timestamp));
        Path outPath = Paths.get(BASE_STORE_PATH, username, "converted", filename);

        if (Files.notExists(outPath.getParent()))
            Files.createDirectories(outPath.getParent());

        FFmpegBuilder builder = new FFmpegBuilder()

                .setInput(probeResult)     // Filename, or a FFmpegProbeResult
                .overrideOutputFiles(true) // Override the output if it exists

                .addOutput(outPath.toString())   // Filename for the destination
                .setFormat("mp4")        // Format is inferred from filename, or can be set
                .setTargetSize(targetSize)  // Aim for a 20MB file

                .setAudioChannels(2)         // Mono audio
                .setAudioCodec("aac")        // using the aac codec
                .setAudioSampleRate(48_000)  // at 48KHz
                .setAudioBitRate(32768)      // at 32 kbit/s

                .setVideoCodec("libx265")     // Video using x265
                .setVideoFrameRate(30, 1)     // at 30 frames per second
                .setVideoResolution(resolution.abbreviation) // at given resolution

                .setStrict(FFmpegBuilder.Strict.NORMAL)
                .done();

        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg);

        // Run a one-pass encode
        FFmpegJob job = executor.createJob(builder/*, new ProgressListener() {

            // Using the FFmpegProbeResult determine the duration of the input
            final double duration_ns = probeResult.getFormat().duration * TimeUnit.SECONDS.toNanos(1);


            @Override
            public void progress(Progress progress) {
                double percentage = progress.out_time_ns / duration_ns;

                // Print out interesting information about the progress
                System.out.printf(
                        "[%.0f%%] status:%s frame:%d time:%s ms fps:%.0f speed:%.2fx%n",
                        percentage * 100,
                        progress.status,
                        progress.frame,
                        FFmpegUtils.toTimecode(progress.out_time_ns, TimeUnit.NANOSECONDS),
                        progress.fps.doubleValue(),
                        progress.speed
                );
            }
        }*/);
        job.run();

        deleteService.recordVideoProcessed(pathToFile);
    }


    public enum VideoResolutions {
        R1080p(1920, 1080),
        R720p(1280, 720),
        R360p(640, 360),
        R240p(426, 240);

        public final String abbreviation;
        private final int height;


        VideoResolutions(int width, int height) {
            this.height = height;
            this.abbreviation = String.format("%dx%d", width, height);
        }


        @Override
        public String toString() {
            return String.valueOf(height);
        }
    }
}
