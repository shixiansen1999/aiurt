package com.aiurt.boot.utils;

import cn.hutool.core.date.DateUtil;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class TestMain {
    public static void main(String[] args) {
        Date patrolDate = DateUtil.parseDate(DateUtil.format(new Date(), "yyyy-MM-dd 00:00:00"));
        LocalDateTime localDateTime = patrolDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        Date missDate = Date.from(localDateTime.plusHours(44).atZone(ZoneId.systemDefault()).toInstant());

        System.out.println(patrolDate);

        System.out.println(DateUtil.format(missDate, "yyyy-MM-dd HH:mm:ss"));
    }
}
