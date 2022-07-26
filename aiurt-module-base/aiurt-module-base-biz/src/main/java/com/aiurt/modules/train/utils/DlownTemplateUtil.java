package com.aiurt.modules.train.utils;

import cn.hutool.core.io.IoUtil;
import org.jeecg.common.exception.JeecgBootException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 模板下载
 * @author fgw
 */
public class DlownTemplateUtil {

    public static String downloadTemplet(HttpServletRequest request, HttpServletResponse response, String fileName) {
        InputStream inputStream = DlownTemplateUtil.class.getResourceAsStream("/templates/" + fileName);
        // 输出文件
        try (OutputStream outputStream = response.getOutputStream()) {
            response.setContentType("application/force-download");
            response.addHeader("Content-Disposition", "attachment;fileName=" + new String(fileName.getBytes("UTF-8"), "iso-8859-1"));
            IoUtil.copy(inputStream, outputStream, IoUtil.DEFAULT_BUFFER_SIZE);
        } catch (IOException e) {
            response.setContentType("application/json;charset=UTF-8");
            throw new JeecgBootException("文件下载失败！文件不存在或已经被删除。");
        }
        return fileName;
    }
}
