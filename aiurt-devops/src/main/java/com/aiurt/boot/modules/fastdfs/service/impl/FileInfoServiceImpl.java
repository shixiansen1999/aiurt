//package com.aiurt.boot.modules.fastdfs.service.impl;
//
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import com.github.tobato.fastdfs.domain.fdfs.StorePath;
//import com.github.tobato.fastdfs.domain.upload.FastFile;
//import com.github.tobato.fastdfs.service.FastFileStorageClient;
//import com.aiurt.boot.modules.fastdfs.FileUtil;
//import com.aiurt.boot.modules.fastdfs.entity.FileInfo;
//import com.aiurt.boot.modules.fastdfs.mapper.FileInfoMapper;
//import com.aiurt.boot.modules.fastdfs.model.UploadFile;
//import com.aiurt.boot.modules.fastdfs.service.IFileInfoService;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.io.FilenameUtils;
//import org.apache.commons.lang.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.ByteArrayInputStream;
//import java.io.InputStream;
//
///**
// * @Description: 附件表
// * @Author: swsc
// * @Date: 2020-10-23
// * @Version: V1.0
// */
//@Service
//@Slf4j
//public class FileInfoServiceImpl extends ServiceImpl<FileInfoMapper, FileInfo> implements IFileInfoService {
//    @Autowired
//    private FastFileStorageClient storageClient;
//    @Autowired
//    private FileInfoMapper fileInfoMapper;
//    private static final String FILE_SPLIT = ".";
//
//    @Value("${fdfs.webUrl}")
//    private String webUrl;
//
//    @Override
//    public FileInfo uploadFile(MultipartFile file) throws Exception {
//        FileInfo fileInfo = FileUtil.getFileInfo(file);
//        FileInfo oldFileInfo = fileInfoMapper.selectOne(new QueryWrapper<FileInfo>().eq("md5", fileInfo.getMd5()));
//        if (oldFileInfo != null) {
//            return oldFileInfo;
//        }
//        StorePath storePath = storageClient.uploadFile(file.getInputStream(), file.getSize(), FilenameUtils.getExtension(file.getOriginalFilename()), null);
//        log.info("upload_path:{}", storePath.getFullPath());
//        log.info("upload_url:{}", webUrl);
//        fileInfo.setUrl(webUrl + "/" + storePath.getFullPath());
//        fileInfo.setPath(storePath.getFullPath());
//
//        fileInfoMapper.insert(fileInfo);
//        return fileInfo;
//    }
//
//    @Override
//    public FileInfo uploadFile(UploadFile uploadFile) {
//        if (StringUtils.isNotBlank(uploadFile.getName()) && !uploadFile.getName().contains(FILE_SPLIT)) {
//            throw new IllegalArgumentException("缺少后缀名");
//        }
//        FileInfo fileInfo = FileUtil.getFileInfo(uploadFile);
//        FileInfo oldFileInfo = fileInfoMapper.selectOne(new QueryWrapper<FileInfo>().eq("md5", fileInfo.getMd5()));
//        if (oldFileInfo != null) {
//            return oldFileInfo;
//        }
//
//        InputStream inputStream = new ByteArrayInputStream(uploadFile.getBytes());
//        FastFile fastFile = (new FastFile.Builder().withFile(inputStream, uploadFile.getSize(), FilenameUtils.getExtension(uploadFile.getName()))).build();
//        StorePath storePath = storageClient.uploadFile(fastFile);
//        fileInfo.setUrl(webUrl + "/" + storePath.getFullPath());
//        fileInfo.setPath(storePath.getFullPath());
//        fileInfoMapper.insert(fileInfo);
//        return fileInfo;
//    }
//
//    @Override
//    public boolean deleteFile(String md5) {
//        FileInfo fileInfo = fileInfoMapper.selectOne(new QueryWrapper<FileInfo>().eq("md5", md5));
//        if (fileInfo != null && StringUtils.isNotEmpty(fileInfo.getPath())) {
//            StorePath storePath = StorePath.parseFromUrl(fileInfo.getPath());
//            storageClient.deleteFile(storePath.getGroup(), storePath.getPath());
//        }
//        fileInfoMapper.deleteById(fileInfo.getId());
//        return true;
//    }
//
//    @Override
//    public boolean deleteFileByPath(String url) {
//        FileInfo fileInfo = fileInfoMapper.selectOne(new QueryWrapper<FileInfo>().eq("url", url));
//        if (fileInfo != null && StringUtils.isNotEmpty(fileInfo.getPath())) {
//            StorePath storePath = StorePath.parseFromUrl(fileInfo.getPath());
//            storageClient.deleteFile(storePath.getGroup(), storePath.getPath());
//        }
//        fileInfoMapper.deleteById(fileInfo.getId());
//        return true;
//    }
//}
