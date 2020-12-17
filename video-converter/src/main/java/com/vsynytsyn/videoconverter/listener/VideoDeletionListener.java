package com.vsynytsyn.videoconverter.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class VideoDeletionListener {

    @RabbitListener(queues = "VideoDeletionQueue")
    public void deleteVideo(String filePath){
        Path pathToFile = Paths.get(filePath);
        try {
            Files.delete(pathToFile);
            System.out.printf("File deleted: %s\n", pathToFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
