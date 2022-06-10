package com.aiurt.boot.modules.system.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

/**
 * ID生成策略工具类
 */
public class IdGenerator {

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    private static SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss");

    public static String uuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * @Description: 生成规则ID
     */
    public static String getId() {
        String t = sdf.format(new Date());
        String uuid = UUID.randomUUID().toString();
        String[] uuids = uuid.split("-");
        return t + uuids[3].substring(1) + uuids[4];
    }

    /**
     * @Description: 投资编号、产品编号、申请编号
     */
    public static String getNo() {
        String t = sdf.format(new Date());
        int r1 = (int) (Math.random() * (10));
        int r2 = (int) (Math.random() * (10));
        int r3 = (int) (Math.random() * (10));
        return t + r1 + r2 + r3;
    }

    /**
     * 三位随机数
     * @return
     */
    public static String getRum(String number) {
        number = "0000000000" + number;
        return "A"+number.substring(number.length()-9,number.length());
    }
    public static String getRum() {
        String number = getNo();
        return "A"+number.substring(number.length()-11,number.length());
    }
    public static String getNo(String pro) {
        String number = getNo();
        return pro+number.substring(number.length()-11,number.length());
    }
    public static String getNumber(int size) {
        String str = "0123456789";
        String number = "";
        Random r = new Random();
        for (int i = 0; i < size; i++) {
            number += str.charAt(r.nextInt(str.length()));
        }
        return number;
    }
    public static String getymdhmsNo() {
        return yyyyMMddHHmmss.format(new Date());
    }
 /*   public static void main(String [] args) {
        System.out.println(getId());
        System.out.println(getNo());

        System.out.println(t);
    }*/
}
