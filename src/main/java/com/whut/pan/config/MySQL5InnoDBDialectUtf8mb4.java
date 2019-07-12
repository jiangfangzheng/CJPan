package com.whut.pan.config;

import org.hibernate.dialect.MySQL5InnoDBDialect;

/**
 * Created by zc on 2019/3/29.
 */
public class MySQL5InnoDBDialectUtf8mb4 extends MySQL5InnoDBDialect {
    @Override
    public String getTableTypeString() {
        return "ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE utf8_unicode_ci";
    }
}
