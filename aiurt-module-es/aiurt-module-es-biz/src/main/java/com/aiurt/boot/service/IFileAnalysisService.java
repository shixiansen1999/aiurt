package com.aiurt.boot.service;

import org.springframework.web.multipart.MultipartFile;

public interface IFileAnalysisService {
    /**
     * 解析并保存上传文件数据
     *
     * @param file
     * @param path
     * @return
     */
    String upload(MultipartFile file, String path);
}
