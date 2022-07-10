package com.aiurt.modules.system.controller;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.util.*;
import com.aiurt.modules.basic.entity.SysAttachment;
import com.aiurt.modules.basic.service.ISysAttachmentService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.exception.JeecgBootException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.util.Objects;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @Author scott
 * @since 2018-12-20
 */
@Slf4j
@RestController
@RequestMapping("/sys/common")
@Api(tags = "文件上传下载")
public class CommonController {

    @Value(value = "${jeecg.path.upload}")
    private String uploadpath;

    /**
     * 本地：local minio：minio 阿里：alioss
     */
    @Value(value="${jeecg.uploadType}")
    private String uploadType;

    @Autowired
    private ISysAttachmentService sysAttachmentService;

    /**
     * @Author 政辉
     * @return
     */
    @GetMapping("/403")
    public Result<?> noauth()  {
        return Result.error("没有权限，请联系管理员授权");
    }

    /**
     * 文件上传统一方法
     * @param request
     * @param response
     * @return
     */
    @PostMapping(value = "/upload")
    @ApiOperation("文件上传")
    @AutoLog("文件上传")
    @ApiImplicitParams({
            @ApiImplicitParam(name="biz",value="业务路径",required=false,paramType="query"),
            @ApiImplicitParam(name="file",value="文件",required=true,paramType="form")
    })
    public Result<?> upload(HttpServletRequest request, HttpServletResponse response) {
        Result<SysAttachment> result = new Result<>();
        String savePath = "";
        String bizPath = request.getParameter("biz");

        String type = uploadType;

        //LOWCOD-2580 sys/common/upload接口存在任意文件上传漏洞
        if (oConvertUtils.isNotEmpty(bizPath) && (bizPath.contains("../") || bizPath.contains("..\\"))) {
            throw new AiurtBootException("上传目录bizPath，格式非法！");
        }

        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        // 获取上传文件对象
        MultipartFile file = multipartRequest.getFile("file");
        if(oConvertUtils.isEmpty(bizPath)){
            if(CommonConstant.UPLOAD_TYPE_OSS.equals(uploadType)){
                //未指定目录，则用阿里云默认目录 upload
                bizPath = "upload";
            }else{
                bizPath = "";
            }
        }
        if(CommonConstant.UPLOAD_TYPE_LOCAL.equals(uploadType)){
            //update-begin-author:lvdandan date:20200928 for:修改JEditor编辑器本地上传
            savePath = this.uploadLocal(file,bizPath);
            //update-begin-author:lvdandan date:20200928 for:修改JEditor编辑器本地上传
        }else{
            //update-begin-author:taoyan date:20200814 for:文件上传改造
            savePath = CommonUtils.upload(file, bizPath, uploadType);
            //update-end-author:taoyan date:20200814 for:文件上传改造
            if (StrUtil.isBlank(savePath)) {
                // 上传失败后
                type = CommonConstant.UPLOAD_TYPE_LOCAL;
                savePath = this.uploadLocal(file,bizPath);
            }
        }
        if(oConvertUtils.isNotEmpty(savePath)){

            // 文件名称
            String originalFilename = file.getOriginalFilename();
            // 保存到系统文件附件库
            SysAttachment sysAttachment = new SysAttachment();
            sysAttachment.setFilePath(savePath);
            sysAttachment.setFileName(originalFilename);
            sysAttachment.setFileType(FilenameUtils.getExtension(originalFilename));
            sysAttachment.setType(type);
            sysAttachment.setDelFlag(0);
            sysAttachmentService.save(sysAttachment);
            String filePathId = String.format("%s?fileName=%s", sysAttachment.getId(), originalFilename);
            result.setMessage(filePathId);
            result.setSuccess(true);
            result.setResult(sysAttachment);
        }else {
            result.setMessage("上传失败！");
            result.setSuccess(false);
        }
        return result;
    }

    /**
     * 本地文件上传
     * @param mf 文件
     * @param bizPath  自定义路径
     * @return
     */
    private String uploadLocal(MultipartFile mf,String bizPath){
        try {
            String ctxPath = uploadpath;
            String fileName = null;
            File file = new File(ctxPath + File.separator + bizPath + File.separator );
            if (!file.exists()) {
                // 创建文件根目录
                file.mkdirs();
            }
            // 获取文件名
            String orgName = mf.getOriginalFilename();
            orgName = CommonUtils.getFileName(orgName);
            if(orgName.indexOf(".")!=-1){
                fileName = orgName.substring(0, orgName.lastIndexOf(".")) + "_" + System.currentTimeMillis() + orgName.substring(orgName.lastIndexOf("."));
            }else{
                fileName = orgName+ "_" + System.currentTimeMillis();
            }
            String savePath = file.getPath() + File.separator + fileName;
            File savefile = new File(savePath);
            FileCopyUtils.copy(mf.getBytes(), savefile);
            String dbpath = null;
            if(oConvertUtils.isNotEmpty(bizPath)){
                dbpath = bizPath + File.separator + fileName;
            }else{
                dbpath = fileName;
            }
            if (dbpath.contains("\\")) {
                dbpath = dbpath.replace("\\", "/");
            }
            return dbpath;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return "";
    }


    /**
     * 预览图片&下载文件
     * 请求地址：http://localhost:8080/common/static/{user/20190119/e1fe9925bc315c60addea1b98eb1cb1349547719_1547866868179.jpg}
     *
     * @param request
     * @param response
     */
    @ApiOperation("文件下载")
    @AutoLog("文件下载")
    @GetMapping(value = "/static/**")
    public void view(HttpServletRequest request, HttpServletResponse response) {
        // ISO-8859-1 ==> UTF-8 进行编码转换
        String imgPath = extractPathFromPattern(request);
        String fileName = request.getParameter("fileName");
        if(oConvertUtils.isEmpty(imgPath) || imgPath=="null"){
            return;
        }
        // 其余处理略
        InputStream inputStream = null;
        OutputStream outputStream = null;

        imgPath = imgPath.replace("..", "").replace("../","");
        if (imgPath.endsWith(",")) {
            imgPath = imgPath.substring(0, imgPath.length() - 1);
        }

        SysAttachment sysAttachment = sysAttachmentService.getById(imgPath);

        if (Objects.isNull(sysAttachment)) {
            downloadLocalFile(response, imgPath, "");
            return;
        }

        if (StrUtil.isBlank(fileName)) {
            fileName = sysAttachment.getFileName();
        }
        // minio存储
        if (StrUtil.equalsIgnoreCase("minio",sysAttachment.getType())) {
            try (
                    InputStream inputStream1 = MinioUtil.getMinioFile("platform",sysAttachment.getFilePath());
                    OutputStream outputStream1 = response.getOutputStream()) {

                response.setContentType("application/force-download");
                response.addHeader("Content-Disposition", "attachment;fileName=" + new String(fileName.getBytes("UTF-8"), "iso-8859-1"));
                IoUtil.copy(inputStream1, outputStream1, IoUtil.DEFAULT_BUFFER_SIZE);
            } catch (IOException e) {
                log.error("预览文件失败" + e.getMessage());
                response.setContentType("application/json;charset=UTF-8");
                throw new AiurtBootException("文件下载失败！文件不存在或已经被删除。");
            }
        }else{
            try {

                String filePath = uploadpath + File.separator + sysAttachment.getFilePath();
                File file = new File(filePath);
                if(!file.exists()){
                    response.setStatus(404);
                    throw new RuntimeException("文件["+imgPath+"]不存在..");
                }


                downloadLocalFile(response, sysAttachment.getFilePath(), fileName);
            } catch (Exception e) {
                log.error("预览文件失败" + e.getMessage());
                response.setStatus(404);
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        log.error(e.getMessage(), e);
                    }
                }
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
        }

    }

    private void downloadLocalFile(HttpServletResponse response, String imgPath, String fileName) {
        String filePath = uploadpath + File.separator + imgPath;
        File file = new File(filePath);
        if (!file.exists()) {
            throw new JeecgBootException("文件不存在..");
        }
        if (StrUtil.isBlank(fileName)) {
            fileName = file.getName();
        }
        try (
                OutputStream outputStream = response.getOutputStream();
                InputStream inputStream = new BufferedInputStream(new FileInputStream(filePath))) {
            response.setContentType("application/force-download");// 设置强制下载不打开
            response.addHeader("Content-Disposition", "attachment;fileName=" + new String(fileName.getBytes("UTF-8"), "iso-8859-1"));
            byte[] buf = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, len);
            }
            response.flushBuffer();
        } catch (IOException e) {
            log.error("预览文件失败" + e.getMessage());
            response.setContentType("application/json;charset=UTF-8");
            throw new JeecgBootException("文件下载失败！文件不存在或已经被删除。");
        }
    }


    /**
     * @功能：pdf预览Iframe
     * @param modelAndView
     * @return
     */
    @RequestMapping("/pdf/pdfPreviewIframe")
    public ModelAndView pdfPreviewIframe(ModelAndView modelAndView) {
        modelAndView.setViewName("pdfPreviewIframe");
        return modelAndView;
    }

    /**
     *  把指定URL后的字符串全部截断当成参数
     *  这么做是为了防止URL中包含中文或者特殊字符（/等）时，匹配不了的问题
     * @param request
     * @return
     */
    private static String extractPathFromPattern(final HttpServletRequest request) {
        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        return new AntPathMatcher().extractPathWithinPattern(bestMatchPattern, path);
    }

    /**
     * 中转HTTP请求，解决跨域问题
     *
     * @param url 必填：请求地址
     * @return
     */
    @RequestMapping("/transitRESTful")
    public Result transitRESTful(@RequestParam("url") String url, HttpServletRequest request) {
        try {
            ServletServerHttpRequest httpRequest = new ServletServerHttpRequest(request);
            // 中转请求method、body
            HttpMethod method = httpRequest.getMethod();
            JSONObject params;
            try {
                params = JSON.parseObject(JSON.toJSONString(httpRequest.getBody()));
            } catch (Exception e) {
                params = new JSONObject();
            }
            // 中转请求问号参数
            JSONObject variables = JSON.parseObject(JSON.toJSONString(request.getParameterMap()));
            variables.remove("url");
            // 在 headers 里传递Token
            String token = TokenUtils.getTokenByRequest(request);
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Access-Token", token);
            // 发送请求
            String httpURL = URLDecoder.decode(url, "UTF-8");
            ResponseEntity<String> response = RestUtil.request(httpURL, method, headers , variables, params, String.class);
            // 封装返回结果
            Result<Object> result = new Result<>();
            int statusCode = response.getStatusCodeValue();
            result.setCode(statusCode);
            result.setSuccess(statusCode == 200);
            String responseBody = response.getBody();
            try {
                // 尝试将返回结果转为JSON
                Object json = JSON.parse(responseBody);
                result.setResult(json);
            } catch (Exception e) {
                // 转成JSON失败，直接返回原始数据
                result.setResult(responseBody);
            }
            return result;
        } catch (Exception e) {
            log.debug("中转HTTP请求失败", e);
            return Result.error(e.getMessage());
        }
    }

}
