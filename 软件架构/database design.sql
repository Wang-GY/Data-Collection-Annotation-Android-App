-- MySQL Script generated by MySQL Workbench
-- Fri Apr  6 21:09:49 2018
-- Model: New Model    Version: 1.0
-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema data_collection_annotation_app
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `data_collection_annotation_app` ;

-- -----------------------------------------------------
-- Schema data_collection_annotation_app
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `data_collection_annotation_app` DEFAULT CHARACTER SET utf8 ;
USE `data_collection_annotation_app` ;

-- -----------------------------------------------------
-- Table `data_collection_annotation_app`.`users`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `data_collection_annotation_app`.`users` ;

CREATE TABLE IF NOT EXISTS `data_collection_annotation_app`.`users` (
  `userid` INT(11) NOT NULL AUTO_INCREMENT,
  `email` VARCHAR(255) CHARACTER SET 'utf8' NOT NULL,
  `hashed_password` VARCHAR(255) CHARACTER SET 'utf8' NOT NULL,
  `nick_name` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `gender` INT(1) NULL DEFAULT NULL COMMENT '0:female\n1:male',
  `register_date` DATETIME NULL DEFAULT NULL,
  `level` INT(1) NOT NULL DEFAULT '0',
  `phone` VARCHAR(45) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `avatar` VARCHAR(45) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `privilege` INT(1) NOT NULL DEFAULT '0' COMMENT '0:finish task\n1:create task',
  `credit` INT(11) NULL DEFAULT NULL,
  `balance` INT(11) NULL DEFAULT NULL,
  PRIMARY KEY (`userid`))
ENGINE = InnoDB
AUTO_INCREMENT = 11
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_czech_ci;


-- -----------------------------------------------------
-- Table `data_collection_annotation_app`.`tasks`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `data_collection_annotation_app`.`tasks` ;

CREATE TABLE IF NOT EXISTS `data_collection_annotation_app`.`tasks` (
  `taskid` INT(11) NOT NULL,
  `name` VARCHAR(45) NOT NULL,
  `description` VARCHAR(500) NOT NULL,
  `start_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '任务提交（开始时间）',
  `type` INT(1) NOT NULL,
  `size` INT(11) NOT NULL COMMENT '图片数量',
  `data_path` VARCHAR(45) NULL DEFAULT NULL COMMENT '数据存放路径\n数据包括：图片、每张图片对应的xm\'l、任务的标签文件（xml）',
  `creater` INT(11) NOT NULL,
  PRIMARY KEY (`taskid`),
  INDEX `to_user_idx` (`creater` ASC),
  CONSTRAINT `to user`
    FOREIGN KEY (`creater`)
    REFERENCES `data_collection_annotation_app`.`users` (`userid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COMMENT = '存储发布的标注或采集任务';


-- -----------------------------------------------------
-- Table `data_collection_annotation_app`.`commits`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `data_collection_annotation_app`.`commits` ;

CREATE TABLE IF NOT EXISTS `data_collection_annotation_app`.`commits` (
  `commitid` INT(11) NOT NULL AUTO_INCREMENT,
  `commit_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `task` INT(11) NOT NULL,
  `size` INT(11) NOT NULL COMMENT '提交图片数量',
  `commiter` INT(11) NOT NULL,
  PRIMARY KEY (`commitid`),
  INDEX `to task_idx` (`task` ASC),
  INDEX `commiter_idx` (`commiter` ASC),
  CONSTRAINT `commiter`
    FOREIGN KEY (`commiter`)
    REFERENCES `data_collection_annotation_app`.`users` (`userid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `to task`
    FOREIGN KEY (`task`)
    REFERENCES `data_collection_annotation_app`.`tasks` (`taskid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `data_collection_annotation_app`.`commit_data`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `data_collection_annotation_app`.`commit_data` ;

CREATE TABLE IF NOT EXISTS `data_collection_annotation_app`.`commit_data` (
  `commitid` INT(11) NOT NULL,
  `item_path` VARCHAR(45) NOT NULL COMMENT '提交的某个图片路径，可以由此访问到图片和对应的xml',
  UNIQUE INDEX `commit_path` (`commitid` ASC, `item_path` ASC),
  CONSTRAINT `commits`
    FOREIGN KEY (`commitid`)
    REFERENCES `data_collection_annotation_app`.`commits` (`commitid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COMMENT = 'relationship between pictures and commits';


-- -----------------------------------------------------
-- Table `data_collection_annotation_app`.`task_label`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `data_collection_annotation_app`.`task_label` ;

CREATE TABLE IF NOT EXISTS `data_collection_annotation_app`.`task_label` (
  `taskid` INT(11) NOT NULL,
  `label` VARCHAR(45) NOT NULL COMMENT '标签内容',
  UNIQUE INDEX `task_label` (`label` ASC, `taskid` ASC),
  INDEX `task_idx` (`taskid` ASC),
  CONSTRAINT `task`
    FOREIGN KEY (`taskid`)
    REFERENCES `data_collection_annotation_app`.`tasks` (`taskid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COMMENT = '可以通过标签查找任务，也解决了任务标签的存储问题';


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
