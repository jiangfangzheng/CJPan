/*
Navicat MySQL Data Transfer

Source Server         : mysql
Source Server Version : 50717
Source Host           : localhost:3306
Source Database       : pan

Target Server Type    : MYSQL
Target Server Version : 50717
File Encoding         : 65001

Date: 2019-06-28 19:08:15
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for link_secret
-- ----------------------------
DROP TABLE IF EXISTS `link_secret`;
CREATE TABLE `link_secret` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `download_num` int(11) NOT NULL,
  `expire_date` datetime DEFAULT NULL,
  `file_name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `local_link` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `secret` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `secret_link` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `share_date` datetime DEFAULT NULL,
  `user_name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Records of link_secret
-- ----------------------------
INSERT INTO `link_secret` VALUES ('1', '1', '2019-06-25 16:48:56', 'CNN-homework.7z', '/data/share/cflower/CNN-homework.7z', 'US1F', 'uhri5n9qKh1IRC9D2OwoXvlAqqQ8mlmO', null, 'cflower');

-- ----------------------------
-- Table structure for pan_save
-- ----------------------------
DROP TABLE IF EXISTS `pan_save`;
CREATE TABLE `pan_save` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `file_name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `local_link` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `pan_path` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `user_name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Records of pan_save
-- ----------------------------

-- ----------------------------
-- Table structure for pan_user
-- ----------------------------
DROP TABLE IF EXISTS `pan_user`;
CREATE TABLE `pan_user` (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `username` varchar(255) DEFAULT NULL COMMENT 'username',
  `password` varchar(255) DEFAULT NULL COMMENT 'password',
  `level` varchar(255) DEFAULT '1' COMMENT 'level',
  `email` varchar(255) DEFAULT NULL COMMENT 'email',
  `phone` varchar(255) DEFAULT NULL COMMENT 'phone',
  `alias` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=MyISAM AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of pan_user
-- ----------------------------
INSERT INTO `pan_user` VALUES ('1', 'admin', '464A4CD851009450D6398D7BE4D62083175C4D', '0', 'sandeepin@qq.com', '15578352978', null);
INSERT INTO `pan_user` VALUES ('2', 'sandeepin', '464A4CD851009450D6398D7BE4D62083175C4D', '0', 'jfz@jfz.me', '17671766376', null);
INSERT INTO `pan_user` VALUES ('3', 'cflower', '464A4CD851009450D6398D7BE4D62083175C4D', '0', 'xxx@qq.com', '18200000000', null);
INSERT INTO `pan_user` VALUES ('4', 'zc2', '464A4CD851009450D6398D7BE4D62083175C4D', '0', 'xxx@qq.com', '18200000000', null);

-- ----------------------------
-- Table structure for verify_code
-- ----------------------------
DROP TABLE IF EXISTS `verify_code`;
CREATE TABLE `verify_code` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `custom_name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `date` datetime DEFAULT NULL,
  `operate_person` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `register_code` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `state` bit(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Records of verify_code
-- ----------------------------
