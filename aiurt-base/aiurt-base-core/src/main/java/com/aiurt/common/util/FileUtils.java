package com.aiurt.common.util;



import com.aiurt.common.exception.AiurtBootException;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: km
 * DateTime: 2021/9/25 19:05
 */
public class FileUtils {
    public static void download(HttpServletResponse response, String filePath, String fileName) throws IOException {
        InputStream is = null;
        try {
            response.setHeader("content-type", "application/octet-stream");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName,"UTF-8"));
            File file = new File(filePath);
            is= new BufferedInputStream(new FileInputStream(file));

            writeBytes(is, response.getOutputStream());
        }catch (Exception e) {
            throw new AiurtBootException("文件下载错误");

        }finally {
            is.close();
        }
    }

    private static void writeBytes(InputStream is, OutputStream os) {
        try {
            byte[] buf = new byte[1024];
            int len = 0;
            while((len = is.read(buf))!=-1)
            {
                os.write(buf,0,len);
            }
        }catch (Exception e) {
            throw new AiurtBootException("文件下载错误");

        }finally {
            if(is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
