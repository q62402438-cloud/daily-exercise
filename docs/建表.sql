-- 建议使用 Navicat 或命令行执行
-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS `daily_exercise_db` 
DEFAULT CHARACTER SET utf8mb4 
COLLATE utf8mb4_general_ci;

USE `daily_exercise_db`;

-- --------------------------------------------------------
-- 1. 用户表 (User)
-- 截图依据：1、User类
-- --------------------------------------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `userID` INT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID，主键自增',
  `userPassword` VARCHAR(200) NOT NULL COMMENT '登录密码'
) COMMENT='用户基类，存储用户通用信息';

-- --------------------------------------------------------
-- 2. 普通用户表 (OrdinaryUser)
-- 截图依据：2、OrdinaryUser类
-- --------------------------------------------------------
DROP TABLE IF EXISTS `ordinary_user`;
CREATE TABLE `ordinary_user` (
  `userID` INT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID，主键自增',
  `userName` VARCHAR(100) COMMENT '用户名',
  `userPassword` VARCHAR(200) NOT NULL COMMENT '登录密码',
  `phoneNumber` VARCHAR(11) UNIQUE COMMENT '手机号，唯一',
  `userMailbox` VARCHAR(100) UNIQUE COMMENT '邮箱，唯一',
  `gender` VARCHAR(10) COMMENT '性别',
  `birthday` DATETIME COMMENT '生日',
  `registerTime` DATETIME COMMENT '注册时间',
  `age` INT COMMENT '年龄',
  `weight` FLOAT COMMENT '体重',
  -- 外键约束：关联 user 表（如果需要，但截图中未明确，可根据设计决定是否保留）
  CONSTRAINT `fk_ordinary_user_user` FOREIGN KEY (`userID`) REFERENCES `user`(`userID`)
) COMMENT='普通用户表，继承 User 类，存储额外个人信息';

-- --------------------------------------------------------
-- 3. 管理员表 (Administrator)
-- 截图依据：3、Administrator类
-- --------------------------------------------------------
DROP TABLE IF EXISTS `administrator`;
CREATE TABLE `administrator` (
  `userID` INT PRIMARY KEY AUTO_INCREMENT COMMENT '管理员ID，主键自增',
  `userPassword` VARCHAR(200) NOT NULL COMMENT '登录密码',
  -- 外键约束：关联 user 表
  CONSTRAINT `fk_administrator_user` FOREIGN KEY (`userID`) REFERENCES `user`(`userID`)
) COMMENT='管理员表，继承 User 类';

-- --------------------------------------------------------
-- 4. 运动项目表 (SportsEvent)
-- 截图依据：4、SportsEvent类
-- --------------------------------------------------------
DROP TABLE IF EXISTS `sports_event`;
CREATE TABLE `sports_event` (
  `EventID` INT PRIMARY KEY AUTO_INCREMENT COMMENT '运动项目ID，主键自增',
  `sportName` VARCHAR(200) COMMENT '项目名称',
  `averageCalorie` INT COMMENT '平均消耗卡路里'
  -- 注意：截图中无计量单位字段，如需可后续添加
) COMMENT='运动项目表，定义系统支持的运动类型及其消耗';

-- --------------------------------------------------------
-- 5. 打卡记录表 (ExerciseRecord)
-- 截图依据：5、ExerciseRecord类
-- --------------------------------------------------------
DROP TABLE IF EXISTS `exercise_record`;
CREATE TABLE `exercise_record` (
  `RecordID` INT PRIMARY KEY AUTO_INCREMENT COMMENT '打卡记录ID，主键自增',
  `userID` INT NOT NULL COMMENT '用户ID，外键',
  `sportsDate` DATETIME COMMENT '运动日期',
  `EventID` INT COMMENT '运动项目ID，外键',
  `StartTime` DATETIME COMMENT '开始时间',
  `EndTime` DATETIME COMMENT '结束时间',
  `exerciseDuration` INT COMMENT '运动时长（分钟）',
  `exerciseAmount` FLOAT COMMENT '运动量（个数或距离）',
  `calorie` INT COMMENT '卡路里消耗',
  -- 外键约束
  CONSTRAINT `fk_exercise_record_user` FOREIGN KEY (`userID`) REFERENCES `ordinary_user`(`userID`),
  CONSTRAINT `fk_exercise_record_event` FOREIGN KEY (`EventID`) REFERENCES `sports_event`(`EventID`)
) COMMENT='打卡记录表，存储用户每次运动的详细数据';

-- --------------------------------------------------------
-- 6. 训练计划表 (TrainingPlan)
-- 截图依据：6、TrainingPlan类
-- --------------------------------------------------------
DROP TABLE IF EXISTS `training_plan`;
CREATE TABLE `training_plan` (
  `planID` INT PRIMARY KEY AUTO_INCREMENT COMMENT '计划ID，主键自增',
  `planName` VARCHAR(200) COMMENT '计划名称',
  `userID` INT COMMENT '创建者ID，外键',
  `planType` TINYINT COMMENT '计划状态：0-未上架，1-已上架',
  `starttime` VARCHAR(20) COMMENT '计划开始时间',
  `endtime` VARCHAR(20) COMMENT '计划结束时间',
  `sportName` VARCHAR(200) COMMENT '运动项目',
  `exerciseAmount` FLOAT COMMENT '每日目标运动量',
  `percentage` FLOAT COMMENT '完成百分比（0-100）',
  -- 外键约束
  CONSTRAINT `fk_training_plan_user` FOREIGN KEY (`userID`) REFERENCES `ordinary_user`(`userID`)
) COMMENT='训练计划表，存储用户创建或系统提供的训练计划';

-- --------------------------------------------------------
-- 7. 主题帖表 (Post)
-- 截图依据：7、Post类
-- --------------------------------------------------------
DROP TABLE IF EXISTS `post`;
CREATE TABLE `post` (
  `PostID` INT PRIMARY KEY AUTO_INCREMENT COMMENT '主题帖ID，主键自增',
  `authorID` INT COMMENT '作者ID，外键',
  `title` VARCHAR(200) COMMENT '帖子标题',
  `content` TEXT COMMENT '帖子内容',
  `publishTime` DATETIME COMMENT '发布时间',
  `AuditState` VARCHAR(20) COMMENT '审核状态',
  -- 外键约束
  CONSTRAINT `fk_post_author` FOREIGN KEY (`authorID`) REFERENCES `ordinary_user`(`userID`)
) COMMENT='主题帖表，存储用户在论坛发布的帖子';

-- --------------------------------------------------------
-- 8. 评论表 (Comment)
-- 截图依据：8、Comment类
-- --------------------------------------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment` (
  `CommentID` INT PRIMARY KEY AUTO_INCREMENT COMMENT '评论ID，主键自增',
  `postID` INT COMMENT '主题帖ID，外键',
  `userID` INT COMMENT '用户ID，外键',
  `content` TEXT COMMENT '评论内容',
  `publishTime` DATETIME COMMENT '发布时间',
  -- 外键约束
  CONSTRAINT `fk_comment_post` FOREIGN KEY (`postID`) REFERENCES `post`(`PostID`),
  CONSTRAINT `fk_comment_user` FOREIGN KEY (`userID`) REFERENCES `ordinary_user`(`userID`)
) COMMENT='评论表，存储用户对主题帖的评论';

-- --------------------------------------------------------
-- 9. 收藏表 (Favorite)
-- 截图依据：9、Favorite类
-- --------------------------------------------------------
DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite` (
  `FavoriteID` INT PRIMARY KEY AUTO_INCREMENT COMMENT '收藏ID，主键自增',
  `userID` INT COMMENT '用户ID，外键',
  `targetID` INT COMMENT '目标ID（收藏的帖子或计划ID）',
  `targetType` VARCHAR(20) COMMENT '目标类型（如：post, plan）',
  `linkUrl` VARCHAR(100) COMMENT '链接URL',
  `favoriteTime` DATETIME COMMENT '收藏时间',
  -- 外键约束
  CONSTRAINT `fk_favorite_user` FOREIGN KEY (`userID`) REFERENCES `ordinary_user`(`userID`)
) COMMENT='收藏表，存储用户收藏的帖子或计划';