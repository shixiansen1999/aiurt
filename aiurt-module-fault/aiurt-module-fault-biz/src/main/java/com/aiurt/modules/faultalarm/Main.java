package com.aiurt.modules.faultalarm;

import liquibase.pro.packaged.S;

import java.io.UnsupportedEncodingException;

/**
 * @author:wgp
 * @create: 2023-06-07 16:44
 * @Description:
 */
public class Main {
    public static void main(String[] args) {
        String fileName = "12号线OMP网络mac调整版.xlsx";
        String a = null;
        try {
            a = new String(fileName.getBytes("UTF-8"), "iso-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println(a);
    }
}
