package com.aiurt.boot.service;

import org.springframework.web.multipart.MultipartFile;

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
}
