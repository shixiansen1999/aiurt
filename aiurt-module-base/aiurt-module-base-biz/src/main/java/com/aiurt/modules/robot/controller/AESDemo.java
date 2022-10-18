package com.aiurt.modules.robot.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wgp
 * @Title:
 * @Description:
 * @date 2022/10/129:18
 */
public class AESDemo {
    public static void main(String[] args) throws Exception {
        String url = "http://172.16.4.253:8189/2022-10-09/400V低压开关柜(2022-10-09 151544)/1P1_红外测温 2022-10-09 151850.jpg";
        // 先替换空格
        url = url.replaceAll(" ", "%20");
        // 中文正则
        String pattern = "[\u4e00-\u9fa5]+";

        // 匹配
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(url);
        StringBuffer stringBuffer = new StringBuffer();
        // m.find()查找
        while (m.find()) {
            // m.start()连续中文的字符串的开始下标， m.end()连续中文的字符串的最后一个字符下标
            String substring = url.substring(m.start(), m.end());
            // m.group()获取字符
            String group = m.group();
            // 中文转义
            String encode = URLEncoder.encode(group, "utf-8");
            m.appendReplacement(stringBuffer, group.replace(substring, encode));
        }
        m.appendTail(stringBuffer);
        System.out.println(stringBuffer);
        download(Arrays.asList(stringBuffer.toString()));

    }

    public static final String savePath = "D://opt//upFiles/a";
    //    public static final String savePath = "D://image";


    public static void download(List<String> urlStringList) {
        OutputStream os = null;
        InputStream is = null;
        try {
            for (String urlString : urlStringList) {
                String fileSubfix = urlString.substring(urlString.lastIndexOf("."), urlString.length()).replace(".", "");
                // 构造URL
                URL url = new URL(urlString);
                // 打开连接
                URLConnection con = url.openConnection();
                //设置请求超时为5s
                con.setConnectTimeout(5 * 1000);
                // 输入流
                is = con.getInputStream();
                // 1K的数据缓冲
                byte[] bs = new byte[1024];
                // 读取到的数据长度
                int len;
                // 路径
                String filename = urlString.substring(urlString.lastIndexOf("/"));
//                System.out.println(filename);
//                String str = urlString.split(filename)[0];
//                System.out.println(str);
//                String[] split = str.split("//");
//                int i = split[1].indexOf("/");
//                String substring = split[1].substring(i);
//                System.out.println(substring);

                // 输出的文件流
                File sf = new File(savePath);
                if (!sf.exists()) {
                    sf.mkdirs();
                }
                os = new FileOutputStream(sf.getPath() + filename + "." + fileSubfix);
                // 开始读取
                while ((len = is.read(bs)) != -1) {
                    os.write(bs, 0, len);
                }
//                System.out.println("下载完成");
            }
            // 完毕，关闭所有链接
            os.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != os) {
                    os.close();
                }
                if (null != is) {
                    is.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
