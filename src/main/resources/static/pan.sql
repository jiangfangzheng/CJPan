
CREATE DATABASE /*!32312 IF NOT EXISTS*/`agentweb` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `pan`;

DROP TABLE IF EXISTS `pan`;

CREATE TABLE `pan_user` (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `username` varchar(255) DEFAULT NULL UNIQUE COMMENT 'username',
  `password` varchar(255) DEFAULT NULL COMMENT 'password',
  `level` varchar(255) DEFAULT '1' COMMENT 'level',
  `email` varchar(255) DEFAULT NULL COMMENT 'email',
  `phone` varchar(255) DEFAULT NULL COMMENT 'phone',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

insert into `pan_user`(`id`,`username`,`password`,`level`,`email`,`phone`) values (1,'admin','123','0','sandeepin@qq.com','15578352978');
insert into `pan_user`(`id`,`username`,`password`,`level`,`email`,`phone`) values (2,'sandeepin','123','0','jfz@jfz.me','17671766376');
insert into `pan_user`(`id`,`username`,`password`,`level`,`email`,`phone`) values (3,'cflower','123','0','xxx@qq.com','18200000000');