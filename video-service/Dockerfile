# Use the official OpenJDK 8 image as the base image
FROM openjdk:8

# Set the working directory to /app
WORKDIR /app

# Copy the Spring Boot jar file into the container at /app/video-service.jar
COPY target/video-service-0.0.1-SNAPSHOT.jar video-service.jar

# Expose port 9001
EXPOSE 9001

# Set the environment variables
#ENV FFMPEG_PATH: /usr/bin/ffmpeg
#ENV dbURL=db \
#    dbUsername=my_username \
#    dbPassword=my_password \
#    FFMPEG_PATH=/usr/bin/ffmpeg

# Install FFmpeg
RUN #apt-get update && \
#    apt-get install -y ffmpeg

# Create a volume to store videos
VOLUME /app/videos

# Start the Spring Boot application
ENTRYPOINT ["java", "-jar", "video-service.jar"]
