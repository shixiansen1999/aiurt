package com.aiurt.boot.service;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author cgkk
 * @Title:
 * @Description:
 * @date 2023/2/159:11
 */
public interface IFileAnalysisService {
    /**
     * 解析并保存上传文件数据
     *
     * @param file
     * @param path
     * @return
     */
    String upload(MultipartFile file, String path, String typeId);

    /**
     * 同步规范知识库的数据和文档数据
     *
     * @param request
     * @param response
     */
    List<String> syncCanonicalKnowledgeBase(HttpServletRequest request, HttpServletResponse response);

    /**
     * 同步数据到es
     * @param index
     */
    void syncData(String index);
}
