# Use the official OpenJDK 8 image as the base image
FROM openjdk:8

# Set the working directory to /app
WORKDIR /app

# Copy the Spring Boot jar file into the container at /app/video-converter.jar
COPY target/video-converter-0.0.1-SNAPSHOT.jar video-converter.jar

# Expose port 9001
EXPOSE 9001

# Set the environment variables
ENV ffmpegPath=/usr/bin/ffmpeg \
    ffprobePath=/usr/bin/ffprobe \
    videoOutputBasePath=/app/videos \
    videoReceiverServiceUrl=http://video-receiver:9001/

# Install FFmpeg
RUN apt-get update && \
    apt-get install -y ffmpeg

# Create a volume to store videos
VOLUME /app/videos

# Start the Spring Boot application
ENTRYPOINT ["java", "-jar", "video-converter.jar"]
