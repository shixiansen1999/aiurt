package com.aiurt.modules.system.controller;

import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.util.CommonUtils;
import com.aiurt.common.util.MinioUtil;
import com.aiurt.common.util.oConvertUtils;
import com.aiurt.modules.oss.entity.OssFile;
import com.aiurt.modules.oss.service.IossFileService;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * minio文件上传示例
 * @author: jeecg-boot
 */
@Slf4j
@RestController
@RequestMapping("/sys/upload")
public class SysUploadController {
    @Autowired
    private IossFileService ossFileService;

    /**
     * 上传
     * @param request
     */
    @PostMapping(value = "/uploadMinio")
    public Result<?> uploadMinio(HttpServletRequest request) {
        Result<?> result = new Result<>();
        String bizPath = request.getParameter("biz");

        //LOWCOD-2580 sys/common/upload接口存在任意文件上传漏洞
        boolean f = oConvertUtils.isNotEmpty(bizPath) && (bizPath.contains("../") || bizPath.contains("..\\"));
        if (f) {
            throw new AiurtBootException("上传目录bizPath，格式非法！");
        }

        if(oConvertUtils.isEmpty(bizPath)){
            bizPath = "";
        }
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        // 获取上传文件对象
        MultipartFile file = multipartRequest.getFile("file");
        // 获取文件名
        String orgName = file.getOriginalFilename();
        orgName = CommonUtils.getFileName(orgName);
        String fileUrl =  MinioUtil.upload(file,bizPath);
        if(oConvertUtils.isEmpty(fileUrl)){
            return Result.error("上传失败,请检查配置信息是否正确!");
        }
        //保存文件信息
        OssFile minioFile = new OssFile();
        minioFile.setFileName(orgName);
        minioFile.setUrl(fileUrl);
        ossFileService.save(minioFile);
        result.setMessage(fileUrl);
        result.setSuccess(true);
        return result;
    }
}
