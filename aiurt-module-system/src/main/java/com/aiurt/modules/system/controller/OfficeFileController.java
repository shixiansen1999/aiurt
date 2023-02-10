package com.aiurt.modules.system.controller;


import cn.hutool.core.util.StrUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.constant.SymbolConstant;
import com.aiurt.common.util.CommonUtils;
import com.aiurt.common.util.MinioUtil;
import com.aiurt.common.util.UUIDGenerator;
import com.aiurt.config.https.HTTPSTrustManager;
import com.aiurt.modules.basic.entity.SysAttachment;
import com.aiurt.modules.basic.service.ISysAttachmentService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.util.Objects;
import java.util.Scanner;

/**
 * @author fgw
 */
@Slf4j
@RestController
@RequestMapping("/sys/common")
@Api(tags = "office文件")
public class OfficeFileController {

    private static final String STATUS = "status";

    private static final String URL = "url";

    /**
     * 2最后一个用户已关闭文档，将编辑后的URL发给业务系统存储文档
     */
    private static final int CLOSE_STATUS = 2;

    /**
     * 6文档正在编辑过程中用户或者业务系统触发强制保存
     */
    private static final int FORCE_SAVE_STATUS = 6;

    @Value(value = "${jeecg.path.upload}")
    private String uploadpath;

    @Value(value = "${jeecg.uploadType}")
    private String uploadType;

    @Autowired
    private ISysAttachmentService sysAttachmentService;



    @RequestMapping(value = "/callback/**", method = {RequestMethod.GET, RequestMethod.POST})
    public void callback(HttpServletRequest request, HttpServletResponse response) throws Exception {
        PrintWriter writer = response.getWriter();
        Scanner scanner = new Scanner(request.getInputStream()).useDelimiter("\\A");
        String body = scanner.hasNext() ? scanner.next() : "";
        log.info("在线文档回调参数->{}", body);
        JSONObject jsonObj = JSONObject.parseObject(body);
        String fileName = request.getParameter("fileName");
        String key = jsonObj.getString("key");
        log.info("文档编辑key->{}", key);

        LambdaQueryWrapper<SysAttachment> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w->w.eq(SysAttachment::getId, key).or().eq(SysAttachment::getDocumentKey, key)).last("limit 1");

        SysAttachment attachment = sysAttachmentService.getOne(wrapper);

        if (Objects.isNull(attachment)) {

            log.info("系统不存在该文件！id->{}", key);
            writer.write("{\"error\":0}");
            return;
        }

        if(jsonObj.getIntValue(STATUS) == CLOSE_STATUS|| jsonObj.getIntValue(STATUS) == FORCE_SAVE_STATUS) {
            String downloadUri = jsonObj.getString(URL);

            if (StrUtil.isBlank(fileName)) {
                fileName = attachment.getFileName();
            }

            if (StrUtil.isBlank(fileName)) {
                fileName = downloadUri.substring(downloadUri.lastIndexOf('/')+1);
            }

            String orgName = CommonUtils.getFileName(fileName);
            if (orgName.contains(SymbolConstant.SPOT)) {
                fileName = orgName.substring(0, orgName.lastIndexOf(".")) + "_" + System.currentTimeMillis() + orgName.substring(orgName.lastIndexOf("."));
            } else {
                fileName = orgName + "_" + System.currentTimeMillis();
            }
            InputStream stream = null;
            java.net.HttpURLConnection connection = null;
            HttpsURLConnection conn = null;
            // 判断是是否为https 请求，不需要证书cert, 否则会报异常unable to find valid certification path to requested target
            if (StrUtil.startWithIgnoreCase(downloadUri, "https")) {
                HTTPSTrustManager.retrieveResponseFromServer(downloadUri);
                URL url = new URL(downloadUri);
                //构造连接
                conn = (HttpsURLConnection) url.openConnection();
                //这个网站要模拟浏览器才行
                conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64; Trident/7.0; rv:11.0) like Gecko");
                //打开连接
                conn.connect();
                //打开这个网站的输入流
                //打开这个网站的输入流
                stream = conn.getInputStream();
            }else {
                URL url = new URL(downloadUri);
                connection = (java.net.HttpURLConnection) url.openConnection();
                //打开连接
                connection.connect();
                stream = connection.getInputStream();
            }


            // 本地存储
            if (CommonConstant.UPLOAD_TYPE_LOCAL.equals(uploadType)) {
                uploadLocal(fileName, attachment, stream);
                // minio
            }else {
                try {
                    MinioUtil.upload(stream, fileName);
                    attachment.setFilePath(fileName);
                } catch (Exception e) {
                   // 上传本地
                    uploadLocal(fileName, attachment, stream);
                    attachment.setType("local");
                }

            }
            attachment.setDocumentKey(UUIDGenerator.generate());
            if (Objects.nonNull(connection)) {
                connection.disconnect();
            }
            /*if (Objects.nonNull(conn)) {
                conn.disconnect();
            }*/
           /* if (Objects.nonNull(stream)) {
                stream.close();
            }*/
        } else if(jsonObj.getIntValue(STATUS) == 3|| jsonObj.getIntValue(STATUS) == 7) {
            writer.write("{\"error\":-1}");
            // 正在编辑
        } else if (jsonObj.getIntValue(STATUS) == 1 ) {

        }
        sysAttachmentService.updateById(attachment);
        writer.write("{\"error\":0}");
    }


    @RequestMapping("/getSysFileKey")
    @ApiOperation("获取在线编辑key")
    public Result<String> getSysFileKey( @ApiParam(name = "id", value = "文件Id")@RequestParam(value = "id", required = false) String id) {
        if (StrUtil.isBlank(id)) {
            return Result.OK(UUIDGenerator.generate());
        }
        SysAttachment sysAttachment = sysAttachmentService.getById(id);
        String documentKey = sysAttachment.getDocumentKey();

        if (StrUtil.isBlank(documentKey)) {
            documentKey = sysAttachment.getId();
        }

        return Result.OK(documentKey);
    }

    private void uploadLocal(String fileName, SysAttachment attachment, InputStream stream) throws IOException {
        String ctxPath = uploadpath;
        File file = new File(ctxPath);
        if (!file.exists()) {
            // 创建文件根目录
            file.mkdirs();
        }
        String savePath = file.getPath() + File.separator + fileName;
        File savedFile = new File(savePath);
        try (FileOutputStream out = new FileOutputStream(savedFile)) {
            int read;
            final byte[] bytes = new byte[1024];
            while ((read = stream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
        }
        attachment.setFilePath(fileName);
    }

    /**
     * 把指定URL后的字符串全部截断当成参数
     * 这么做是为了防止URL中包含中文或者特殊字符（/等）时，匹配不了的问题
     *
     * @param request
     * @return
     */
    private static String extractPathFromPattern(final HttpServletRequest request) {
        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        log.info("path->{}",path);
        String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        return new AntPathMatcher().extractPathWithinPattern(bestMatchPattern, path);
    }
}
