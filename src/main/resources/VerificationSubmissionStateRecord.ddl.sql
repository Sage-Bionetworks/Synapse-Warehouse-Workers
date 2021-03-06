CREATE TABLE IF NOT EXISTS `VERIFICATION_SUBMISSION_STATE_RECORD` (
  `ID` bigint NOT NULL,
  `STATE` ENUM('SUBMITTED', 'REJECTED', 'APPROVED', 'SUSPENDED') NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `CREATED_BY` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`, `STATE`),
  INDEX (CREATED_BY)
)