CREATE TABLE video_thumbnail
(
    hash VARCHAR(255) not null primary key,
    FOREIGN KEY (hash) REFERENCES video_metadata (hash)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);