package com.aiurt.boot.modules.fastdfs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import cn.hutool.core.util.RandomUtil;
import com.swsc.copsms.modules.fastdfs.entity.FileInfo;
import com.swsc.copsms.modules.fastdfs.model.UploadFile;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件工具类
 *
 * @author 作者 owen E-mail: 624191343@qq.com
 */
@Slf4j
public class FileUtil {
    private FileUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static FileInfo getFileInfo(MultipartFile file) throws Exception {
        String md5 = fileMd5(file.getInputStream());
        FileInfo fileInfo = new FileInfo();
        // 将文件的md5设置为文件表的id
        fileInfo.setMd5(md5);
        fileInfo.setName(file.getOriginalFilename());
        fileInfo.setContentType(file.getContentType());
        fileInfo.setIsImg(fileInfo.getContentType().startsWith("image/") ? 1 : 0);
        fileInfo.setSize(file.getSize());
        return fileInfo;
    }

    public static FileInfo getFileInfo(UploadFile file) {
        String md5 = DigestUtils.md5Hex(file.getBytes() + RandomUtil.randomNumbers(5));
        FileInfo fileInfo = new FileInfo();
        // 将文件的md5设置为文件表的id
        fileInfo.setMd5(md5);
        fileInfo.setName(file.getName());
        fileInfo.setContentType(file.getContentType());
        fileInfo.setIsImg(file.getIsImg() ? 1 : 0);
        fileInfo.setSize(file.getSize());
        return fileInfo;
    }

    /**
     * 文件的md5
     *
     * @param inputStream
     * @return
     */
    public static String fileMd5(InputStream inputStream) {
        try {
            return DigestUtils.md5Hex(inputStream);
        } catch (IOException e) {
            log.error("fileMd5-error", e);
        }
        return null;
    }

    public static String saveFile(MultipartFile file, String path) {
        try {
            File targetFile = new File(path);
            if (targetFile.exists()) {
                return path;
            }
            if (!targetFile.getParentFile().exists()) {
                targetFile.getParentFile().mkdirs();
            }
            file.transferTo(targetFile);
            return path;
        } catch (Exception e) {
            log.error("saveFile-error", e);
        }
        return null;
    }

    public static boolean deleteFile(String pathname) {
        File file = new File(pathname);
        if (file.exists()) {
            boolean flag = file.delete();
            if (flag) {
                File[] files = file.getParentFile().listFiles();
                if (files == null || files.length == 0) {
                    file.getParentFile().delete();
                }
            }
            return flag;
        }
        return false;
    }
}
