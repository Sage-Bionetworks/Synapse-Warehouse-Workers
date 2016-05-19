CREATE TABLE IF NOT EXISTS `BULK_FILE_DOWNLOAD_RECORD` (
  `USER_ID` bigint(20) NOT NULL,
  `OBJECT_ID` bigint(20) NOT NULL,
  `OBJECT_TYPE` ENUM ('FileEntity', 'TableEntity', 'WikiAttachment', 'UserProfileAttachment', 'MessageAttachment', 'TeamAttachment', 'SubmissionAttachment', 'VerificationSubmission') NOT NULL,
  PRIMARY KEY (`USER_ID`, `OBJECT_ID`, `OBJECT_TYPE`),
  INDEX (USER_ID),
  INDEX (OBJECT_ID, OBJECT_TYPE)
)
