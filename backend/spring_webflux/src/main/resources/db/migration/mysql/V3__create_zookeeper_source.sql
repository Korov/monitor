DROP TABLE IF EXISTS `zookeeper_source`;
CREATE TABLE `zookeeper_source`
(
    `id`      BIGINT NOT NULL AUTO_INCREMENT,
    `name`    VARCHAR(1022) DEFAULT NULL,
    `address` VARCHAR(1022) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE utf8mb4_bin;