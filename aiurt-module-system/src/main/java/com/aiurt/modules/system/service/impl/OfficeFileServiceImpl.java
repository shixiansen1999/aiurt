package com.aiurt.modules.system.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.EsFileAPI;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.constant.SymbolConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.util.CommonUtils;
import com.aiurt.common.util.MinioUtil;
import com.aiurt.common.util.UUIDGenerator;
import com.aiurt.config.https.HTTPSTrustManager;
import com.aiurt.modules.basic.entity.SysAttachment;
import com.aiurt.modules.basic.service.ISysAttachmentService;
import com.aiurt.modules.search.dto.FileDataDTO;
import com.aiurt.modules.sysfile.entity.SysFile;
import com.aiurt.modules.sysfile.service.ISysFileManageService;
import com.aiurt.modules.sysfile.service.ISysFileService;
import com.aiurt.modules.system.constant.SystemConstant;
import com.aiurt.modules.system.service.IOfficeFileService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.update.UpdateResponse;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

/**
 * @author:wgp
 * @create: 2023-05-30 15:52
 * @Description: 在线编辑处理类
 */
@Slf4j
@Service
public class OfficeFileServiceImpl implements IOfficeFileService {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ISysFileService fileService;
    @Autowired
    private EsFileAPI esFileAPI;
    @Autowired
    private ISysAttachmentService sysAttachmentService;
    @Autowired
    private ISysFileManageService sysFileManageService;
    @Autowired
    private ISysBaseAPI sysBaseApi;

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

    /**
     * 在线编辑回调
     *
     * @param request  表示传入请求的 HttpServletRequest 对象
     * @param response 表示要发送的 HttpServletResponse 对象
     * @throws Exception 如果在处理请求过程中发生错误
     */
    @Override
    public void callback(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter writer = response.getWriter();
        Scanner scanner = new Scanner(request.getInputStream()).useDelimiter("\\A");
        String body = scanner.hasNext() ? scanner.next() : "";
        log.info("在线文档回调参数->{}", body);
        JSONObject jsonObj = JSONObject.parseObject(body);
        String fileName = request.getParameter("fileName");
        String key = jsonObj.getString("key");

        String status = jsonObj.getString("status");
        String userId = null;
        if (SystemConstant.ONLY_OFFICE_CALL_BACK_STATUS_2.equals(status)) {
            JSONArray users = jsonObj.getJSONArray("users");
            if (users != null) {
                userId = (String) users.get(0);
            }
        }

        log.info("文档编辑key->{}", key);

        LambdaQueryWrapper<SysAttachment> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(SysAttachment::getId, key).or().eq(SysAttachment::getDocumentKey, key)).last("limit 1");

        SysAttachment attachment = sysAttachmentService.getOne(wrapper);

        if (Objects.isNull(attachment)) {

            log.info("系统不存在该文件！id->{}", key);
            writer.write("{\"error\":0}");
            return;
        }

        // 更新知识库文件的更新时间
        updateSysFile(attachment.getId(), userId, status);

        byte[] bytes = null;
        if (jsonObj.getIntValue(STATUS) == CLOSE_STATUS || jsonObj.getIntValue(STATUS) == FORCE_SAVE_STATUS) {
            String downloadUri = jsonObj.getString(URL);

            if (StrUtil.isBlank(fileName)) {
                fileName = attachment.getFileName();
            }

            if (StrUtil.isBlank(fileName)) {
                fileName = downloadUri.substring(downloadUri.lastIndexOf('/') + 1);
            }

            String orgName = CommonUtils.getFileName(fileName);
            if (orgName.contains(SymbolConstant.SPOT)) {
                fileName = orgName.substring(0, orgName.lastIndexOf(".")) + "_" + System.currentTimeMillis() + orgName.substring(orgName.lastIndexOf("."));
            } else {
                fileName = orgName + "_" + System.currentTimeMillis();
            }
            InputStream stream = null;
            // 判断是是否为https 请求，不需要证书cert, 否则会报异常unable to find valid certification path to requested target
            if (StrUtil.startWithIgnoreCase(downloadUri, "https")) {
                HTTPSTrustManager.retrieveResponseFromServer(downloadUri);
                HttpHeaders headers = new HttpHeaders();

                HttpEntity<Resource> httpEntity = new HttpEntity<>(headers);

                ResponseEntity<byte[]> fileResponse = restTemplate.exchange(downloadUri, HttpMethod.GET, httpEntity, byte[].class);
                stream = new ByteArrayInputStream(fileResponse.getBody());
            } else {
                HttpHeaders headers = new HttpHeaders();

                HttpEntity<Resource> httpEntity = new HttpEntity<>(headers);

                ResponseEntity<byte[]> fileResponse = restTemplate.exchange(downloadUri, HttpMethod.GET, httpEntity, byte[].class);
                stream = new ByteArrayInputStream(fileResponse.getBody());
            }

            // 本地存储
            if (CommonConstant.UPLOAD_TYPE_LOCAL.equals(uploadType)) {
                uploadLocal(fileName, attachment, stream);
                // minio
            } else {
                try {
                    MinioUtil.upload(stream, fileName);
                    attachment.setFilePath(fileName);
                } catch (Exception e) {
                    log.error(e.getMessage());
                    // 上传本地
                    uploadLocal(fileName, attachment, stream);
                    attachment.setType("local");
                }
            }
            attachment.setDocumentKey(UUIDGenerator.generate());
            if (Objects.nonNull(stream)) {
                stream.close();
            }
        } else if (jsonObj.getIntValue(STATUS) == 3 || jsonObj.getIntValue(STATUS) == 7) {
            writer.write("{\"error\":-1}");
            // 正在编辑
        } else if (jsonObj.getIntValue(STATUS) == 1) {

        }
        sysAttachmentService.updateById(attachment);
        writer.write("{\"error\":0}");

        // 更新ES文档内容
        this.updateEsData(attachment, bytes);
    }

    @Override
    public String getSysFileKey(String id) {
        if (StrUtil.isBlank(id)) {
            return UUIDGenerator.generate();
        }

        SysAttachment sysAttachment = sysAttachmentService.getById(id);
        if (ObjectUtil.isEmpty(sysAttachment)) {
            return UUIDGenerator.generate();
        }

        String documentKey = sysAttachment.getDocumentKey();
        if (StrUtil.isBlank(documentKey)) {
            documentKey = sysAttachment.getId();
        }

        return documentKey;
    }

    /**
     * 更新规范规程知识库的文件
     *
     * @param id     文件id
     * @param userId 用户id
     * @param status onlyoffice回调状态
     */
    private void updateSysFile(String id, String userId, String status) {
        if (!SystemConstant.ONLY_OFFICE_CALL_BACK_STATUS_2.equals(status)) {
            return;
        }

        if (StrUtil.isEmpty(id) || StrUtil.isEmpty(userId)) {
            log.error("文件id为空或人员id为空，更新文件更新时间失败");
            return;
        }

        LambdaQueryWrapper<SysFile> lam = new LambdaQueryWrapper<>();
        lam.likeRight(SysFile::getUrl, id);
        lam.eq(SysFile::getDelFlag, CommonConstant.DEL_FLAG_0);
        lam.last("LIMIT 1");
        SysFile sysFile = sysFileManageService.getOne(lam);

        if (ObjectUtil.isNotEmpty(sysFile)) {
            LoginUser user = sysBaseApi.getUserById(userId);
            if (ObjectUtil.isNotEmpty(user)) {
                sysFile.setUpdateTime(new Date());
                sysFile.setUpdateBy(user.getUsername());
                sysFileManageService.updateById(sysFile);
            }
        }
    }

    /**
     * 同步更新ES的记录信息
     *
     * @param attachment
     */
    private UpdateResponse updateEsData(SysAttachment attachment, byte[] bytes) {
        List<SysFile> fileList = fileService.lambdaQuery().eq(SysFile::getDelFlag, CommonConstant.DEL_FLAG_0).likeRight(SysFile::getUrl, attachment.getId()).list();
        if (ObjectUtil.isEmpty(bytes) || CollectionUtil.isEmpty(fileList)) {
            return null;
        }
        if (1 < fileList.size()) {
            throw new AiurtBootException("文件数据同步到ES异常！文件有多条规范规程关联数据！");
        }
        SysFile file = fileList.stream().findFirst().get();
        FileDataDTO fileDataDTO = new FileDataDTO();
        fileDataDTO.setId(String.valueOf(file.getId()));
        fileDataDTO.setFileBytes(bytes);
        fileDataDTO.setAddress(file.getUrl());
        fileDataDTO.setName(file.getName());
        fileDataDTO.setFormat(file.getType());
        UpdateResponse response = esFileAPI.updateFileData(fileDataDTO);
        return response;
    }

    /**
     * 在本地存储中上传文件。
     *
     * @param fileName   文件名
     * @param attachment SysAttachment对象，表示文件的相关信息
     * @param stream     文件输入流
     * @throws IOException 如果发生I/O错误
     */
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
        log.info("path->{}", path);
        String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        return new AntPathMatcher().extractPathWithinPattern(bestMatchPattern, path);
    }

}
