DROP TABLE IF EXISTS `kafka_source`;
CREATE TABLE `kafka_source`
(
    `id`     BIGINT NOT NULL AUTO_INCREMENT,
    `name`   VARCHAR(1022) DEFAULT NULL,
    `broker` VARCHAR(1022) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE utf8mb4_bin;