-- =======================================================
-- 日常运动系统数据库 - 完整建表语句
-- userType：0-管理员，1-普通用户
-- recordType：0-自由运动，1-计划打卡
-- auditState：0-未审核，1-已通过，2-已拒绝
-- =======================================================

CREATE DATABASE IF NOT EXISTS `daily_exercise_db`
DEFAULT CHARACTER SET utf8mb4
COLLATE utf8mb4_general_ci;

USE `daily_exercise_db`;

-- --------------------------------------------------------
-- 1. 用户表 user（仅登录用）
-- --------------------------------------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `userID` INT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
  `userPassword` VARCHAR(200) NOT NULL COMMENT '登录密码',
  `userType` TINYINT NOT NULL DEFAULT 1 COMMENT '用户类型：0-管理员 1-普通用户'
) COMMENT='用户登录表';

-- --------------------------------------------------------
-- 2. 管理员表 administrator
-- --------------------------------------------------------
DROP TABLE IF EXISTS `administrator`;
CREATE TABLE `administrator` (
  `userID` INT PRIMARY KEY COMMENT '管理员ID，关联 user',
  `userName` VARCHAR(100) COMMENT '用户名',
  `phoneNumber` VARCHAR(11) UNIQUE COMMENT '手机号',
  CONSTRAINT `fk_administrator_user`
    FOREIGN KEY (`userID`) REFERENCES `user`(`userID`)
) COMMENT='管理员表';

-- --------------------------------------------------------
-- 3. 普通用户表 ordinary_user
-- --------------------------------------------------------
DROP TABLE IF EXISTS `ordinary_user`;
CREATE TABLE `ordinary_user` (
  `userID` INT PRIMARY KEY COMMENT '普通用户ID，关联 user',
  `userName` VARCHAR(100) COMMENT '用户名',
  `phoneNumber` VARCHAR(11) UNIQUE COMMENT '手机号',
  `userMailbox` VARCHAR(100) UNIQUE COMMENT '邮箱',
  `gender` VARCHAR(10) COMMENT '性别',
  `birthday` DATETIME COMMENT '生日',
  `registerTime` DATETIME COMMENT '注册时间',
  `age` INT COMMENT '年龄',
  `weight` FLOAT COMMENT '体重',
  CONSTRAINT `fk_ordinary_user_user`
    FOREIGN KEY (`userID`) REFERENCES `user`(`userID`)
) COMMENT='普通用户表';

-- --------------------------------------------------------
-- 4. 运动项目表 sports_event
-- --------------------------------------------------------
DROP TABLE IF EXISTS `sports_event`;
CREATE TABLE `sports_event` (
  `eventID` INT PRIMARY KEY AUTO_INCREMENT COMMENT '运动项目ID',
  `sportName` VARCHAR(200) COMMENT '运动名称',
  `averageCalorie` INT COMMENT '平均消耗卡路里'
) COMMENT='运动项目表';

-- --------------------------------------------------------
-- 5. 打卡记录表 exercise_record
-- --------------------------------------------------------
DROP TABLE IF EXISTS `exercise_record`;
CREATE TABLE `exercise_record` (
  `recordID` INT PRIMARY KEY AUTO_INCREMENT COMMENT '打卡记录ID',
  `userID` INT NOT NULL COMMENT '用户ID',
  `sportsDate` DATE COMMENT '运动日期',
  `eventID` INT COMMENT '运动项目ID/计划ID',
  `startTime` DATETIME COMMENT '开始时间',
  `endTime` DATETIME COMMENT '结束时间',
  `exerciseDuration` INT COMMENT '运动时长（分钟）',
  `exerciseAmount` FLOAT COMMENT '运动量',
  `calorie` FLOAT COMMENT '消耗卡路里',
  `recordType` TINYINT DEFAULT 0 COMMENT '记录类型：0-自由运动 1-计划打卡',
  CONSTRAINT `fk_exercise_record_user`
    FOREIGN KEY (`userID`) REFERENCES `ordinary_user`(`userID`)
) COMMENT='打卡记录表';

-- --------------------------------------------------------
-- 6. 训练计划表 training_plan
-- --------------------------------------------------------
DROP TABLE IF EXISTS `training_plan`;
CREATE TABLE `training_plan` (
  `planID` INT PRIMARY KEY AUTO_INCREMENT COMMENT '计划ID',
  `planName` VARCHAR(200) COMMENT '计划名称',
  `userID` INT COMMENT '创建者ID',
  `planType` TINYINT DEFAULT 0 COMMENT '0-未上架 1-已上架 0-未开始 1-执行中 2-已结束',
  `startTime` DATE COMMENT '开始时间',
  `endTime` DATE COMMENT '结束时间',
  `sportName` VARCHAR(200) COMMENT '运动项目',
  `exerciseAmount` FLOAT COMMENT '每日目标运动量',
  `percentage` FLOAT COMMENT '完成百分比',
  CONSTRAINT `fk_training_plan_user`
    FOREIGN KEY (`userID`) REFERENCES `ordinary_user`(`userID`)
) COMMENT='训练计划表';

-- --------------------------------------------------------
-- 7. 主题帖表 post
-- --------------------------------------------------------
DROP TABLE IF EXISTS `post`;
CREATE TABLE `post` (
  `postID` INT PRIMARY KEY AUTO_INCREMENT COMMENT '帖子ID',
  `authorID` INT COMMENT '作者ID',
  `title` VARCHAR(200) COMMENT '标题',
  `content` TEXT COMMENT '内容',
  `publishTime` DATETIME COMMENT '发布时间',
  `auditState` TINYINT DEFAULT 0 COMMENT '审核状态：0-未审核 1-已通过 2-已拒绝',
  CONSTRAINT `fk_post_author`
    FOREIGN KEY (`authorID`) REFERENCES `ordinary_user`(`userID`)
) COMMENT='主题帖表';

-- --------------------------------------------------------
-- 8. 评论表 comment
-- --------------------------------------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment` (
  `commentID` INT PRIMARY KEY AUTO_INCREMENT COMMENT '评论ID',
  `postID` INT COMMENT '帖子ID',
  `userID` INT COMMENT '用户ID',
  `content` TEXT COMMENT '评论内容',
  `publishTime` DATETIME COMMENT '发布时间',
  CONSTRAINT `fk_comment_post`
    FOREIGN KEY (`postID`) REFERENCES `post`(`postID`),
  CONSTRAINT `fk_comment_user`
    FOREIGN KEY (`userID`) REFERENCES `ordinary_user`(`userID`)
) COMMENT='评论表';

-- --------------------------------------------------------
-- 9. 收藏表 favorite
-- --------------------------------------------------------
DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite` (
  `favoriteID` INT PRIMARY KEY AUTO_INCREMENT COMMENT '收藏ID',
  `userID` INT COMMENT '用户ID',
  `targetID` INT COMMENT '目标ID',
  `targetType` VARCHAR(20) COMMENT '类型：post / plan',
  `linkUrl` VARCHAR(255) COMMENT '链接',
  `favoriteTime` DATETIME COMMENT '收藏时间',
  CONSTRAINT `fk_favorite_user`
    FOREIGN KEY (`userID`) REFERENCES `ordinary_user`(`userID`)
) COMMENT='收藏表';

-- --------------------------------------------------------
-- 插入初始数据
-- --------------------------------------------------------
INSERT INTO `sports_event` (`sportName`, `averageCalorie`) VALUES
('慢走', 4.08),
('快走', 6.42),
('慢跑', 9.69),
('跑步', 11.43),
('游泳', 7.00),
('骑行', 7.93),
('跳绳', 11.67),
('瑜伽', 3.50),
('力量训练', 5.83);