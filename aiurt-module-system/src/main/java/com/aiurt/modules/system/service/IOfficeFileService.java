package com.aiurt.modules.system.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author:wgp
 * @create: 2023-05-30 15:52
 * @Description: 在线编辑接口
 */

public interface IOfficeFileService {
    /**
     * 在线编辑回调
     *
     * @param request  表示传入请求的 HttpServletRequest 对象
     * @param response 表示要发送的 HttpServletResponse 对象
     * @throws IOException 如果在处理请求过程中发生错误
     */
    void callback(HttpServletRequest request, HttpServletResponse response) throws IOException;

    /**
     * 根据文件ID获取系统文件的关键信息。
     *
     * @param id 文件ID
     * @return 系统文件的关键信息
     */
    String getSysFileKey(String id);
}
