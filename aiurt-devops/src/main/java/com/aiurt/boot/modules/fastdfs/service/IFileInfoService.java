package com.aiurt.boot.modules.fastdfs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.swsc.copsms.modules.fastdfs.entity.FileInfo;
import com.swsc.copsms.modules.fastdfs.model.UploadFile;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Description: 附件表
 * @Author: swsc
 * @Date: 2020-10-23
 * @Version: V1.0
 */
public interface IFileInfoService extends IService<FileInfo> {
    FileInfo uploadFile(MultipartFile file) throws Exception;

    FileInfo uploadFile(UploadFile uploadFile);

    boolean deleteFile(String id);

    boolean deleteFileByPath(String url);
}
