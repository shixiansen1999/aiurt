package com.aiurt.modules.oss.service;

import java.io.IOException;

import com.aiurt.modules.oss.entity.OssFile;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Description: OOS云存储service接口
 * @author: jeecg-boot
 */
public interface IossFileService extends IService<OssFile> {

    /**
     * oss文件上传
     * @param multipartFile
     * @throws IOException
     */
	void upload(MultipartFile multipartFile) throws IOException;

    /**
     * oss文件删除
     * @param ossFile OSSFile对象
     * @return
     */
	boolean delete(OssFile ossFile);

}
