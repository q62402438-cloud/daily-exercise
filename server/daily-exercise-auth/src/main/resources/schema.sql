CREATE TABLE IF NOT EXISTS `user` (
    `userID` INT AUTO_INCREMENT PRIMARY KEY,
    `userPassword` VARCHAR(255) NOT NULL,
    `userType` INT NOT NULL DEFAULT 1,
    INDEX `idx_user_type` (`userType`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `administrator` (
    `userID` INT PRIMARY KEY,
    `userName` VARCHAR(100) NOT NULL,
    `phoneNumber` VARCHAR(20) NOT NULL,
    CONSTRAINT `fk_admin_user` FOREIGN KEY (`userID`) REFERENCES `user`(`userID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `ordinary_user` (
    `userID` INT PRIMARY KEY,
    `userName` VARCHAR(100) NOT NULL,
    `phoneNumber` VARCHAR(20) NOT NULL,
    `userMailbox` VARCHAR(100),
    `gender` VARCHAR(10),
    `birthday` DATETIME,
    `registerTime` DATETIME NOT NULL,
    `age` INT,
    `weight` FLOAT,
    CONSTRAINT `fk_ordinary_user` FOREIGN KEY (`userID`) REFERENCES `user`(`userID`) ON DELETE CASCADE,
    UNIQUE INDEX `uk_phone` (`phoneNumber`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `sports_event` (
    `eventID` INT AUTO_INCREMENT PRIMARY KEY,
    `sportName` VARCHAR(100) NOT NULL,
    `averageCalorie` FLOAT NOT NULL,
    UNIQUE INDEX `uk_sport_name` (`sportName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `exercise_record` (
    `recordID` INT AUTO_INCREMENT PRIMARY KEY,
    `userID` INT NOT NULL,
    `sportsDate` VARCHAR(20) NOT NULL,
    `eventID` INT NOT NULL,
    `startTime` DATETIME NOT NULL,
    `endTime` DATETIME NOT NULL,
    `exerciseDuration` INT NOT NULL,
    `exerciseAmount` FLOAT NOT NULL,
    `calorie` FLOAT NOT NULL,
    `recordType` INT NOT NULL DEFAULT 0,
    CONSTRAINT `fk_record_user` FOREIGN KEY (`userID`) REFERENCES `user`(`userID`) ON DELETE CASCADE,
    INDEX `idx_record_date` (`sportsDate`),
    INDEX `idx_record_user` (`userID`),
    INDEX `idx_record_type` (`recordType`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `training_plan` (
    `planID` INT AUTO_INCREMENT PRIMARY KEY,
    `planName` VARCHAR(100) NOT NULL,
    `userID` INT NOT NULL,
    `planType` INT NOT NULL DEFAULT 0,
    `startTime` DATETIME NOT NULL,
    `endTime` DATETIME NOT NULL,
    `sportName` VARCHAR(100) NOT NULL,
    `exerciseAmount` FLOAT NOT NULL,
    `percentage` FLOAT NOT NULL DEFAULT 0,
    CONSTRAINT `fk_plan_user` FOREIGN KEY (`userID`) REFERENCES `user`(`userID`) ON DELETE CASCADE,
    INDEX `idx_plan_user` (`userID`),
    INDEX `idx_plan_type` (`planType`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `post` (
    `postID` INT AUTO_INCREMENT PRIMARY KEY,
    `authorID` INT NOT NULL,
    `title` VARCHAR(200) NOT NULL,
    `content` TEXT NOT NULL,
    `publishTime` DATETIME NOT NULL,
    `auditState` INT NOT NULL DEFAULT 0,
    CONSTRAINT `fk_post_author` FOREIGN KEY (`authorID`) REFERENCES `user`(`userID`) ON DELETE CASCADE,
    INDEX `idx_post_author` (`authorID`),
    INDEX `idx_post_audit` (`auditState`),
    INDEX `idx_post_time` (`publishTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `comment` (
    `commentID` INT AUTO_INCREMENT PRIMARY KEY,
    `postID` INT NOT NULL,
    `userID` INT NOT NULL,
    `content` TEXT NOT NULL,
    `commentTime` DATETIME NOT NULL,
    CONSTRAINT `fk_comment_post` FOREIGN KEY (`postID`) REFERENCES `post`(`postID`) ON DELETE CASCADE,
    CONSTRAINT `fk_comment_user` FOREIGN KEY (`userID`) REFERENCES `user`(`userID`) ON DELETE CASCADE,
    INDEX `idx_comment_post` (`postID`),
    INDEX `idx_comment_time` (`commentTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `favorite` (
    `favoriteID` INT AUTO_INCREMENT PRIMARY KEY,
    `userID` INT NOT NULL,
    `targetID` INT NOT NULL,
    `targetType` INT NOT NULL,
    `linkUrl` VARCHAR(500),
    `favoriteTime` DATETIME NOT NULL,
    CONSTRAINT `fk_favorite_user` FOREIGN KEY (`userID`) REFERENCES `user`(`userID`) ON DELETE CASCADE,
    UNIQUE INDEX `uk_favorite_target` (`userID`, `targetID`, `targetType`),
    INDEX `idx_favorite_type` (`targetType`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
