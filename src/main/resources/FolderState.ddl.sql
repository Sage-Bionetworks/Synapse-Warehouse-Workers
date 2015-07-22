CREATE TABLE IF NOT EXISTS `FOLDER_STATE` (
  `S3_BUCKET` varchar(256) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `S3_PATH` varchar(700) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `STATE` ENUM('ROLLING','COLLATED') NOT NULL,
  `UPDATED_ON` TIMESTAMP NOT NULL,
  PRIMARY KEY (`S3_BUCKET`,`S3_PATH`)
)