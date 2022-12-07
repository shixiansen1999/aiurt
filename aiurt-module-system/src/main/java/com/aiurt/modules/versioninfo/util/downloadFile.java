package com.aiurt.modules.versioninfo.util;

import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

public class downloadFile {
    @Autowired
    private HttpServletRequest request;

    public static void downloadFile(String path, HttpServletResponse response) throws IOException {
        File file = new File(path);
        String filename = file.getName();
        filename = URLEncoder.encode(filename, "UTF-8");
        // 如果文件不存在
        if (!file.exists()) {
            response.sendError(404, "File not found!");
        }
        // 创建一个缓冲输入流对象
        BufferedInputStream br = new BufferedInputStream(new FileInputStream(file));
        byte[] buf = new byte[10240];
        int len = 0;
        response.reset(); // 非常重要
        response.setContentType("application/octet-stream;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + filename);
        // 创建输出流对象
        OutputStream outStream = response.getOutputStream();
        // 开始输出
        while ((len = br.read(buf)) > 0)
            outStream.write(buf, 0, len);
        // 关闭流对象
        br.close();
        outStream.close();
    }
}
