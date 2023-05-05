create table video_metadata
(
    hash            varchar(255) not null primary key,
    author_username varchar(255) not null,
    title           varchar(255) not null,
    description     varchar(255)
);

CREATE TABLE video_resolution
(
    hash       VARCHAR(255) not null,
    resolution_height varchar(255) not null,
    PRIMARY KEY (hash, resolution_height),
    FOREIGN KEY (hash) REFERENCES video_metadata (hash)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);