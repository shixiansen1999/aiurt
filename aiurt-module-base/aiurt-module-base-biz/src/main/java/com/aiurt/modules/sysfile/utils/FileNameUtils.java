package com.aiurt.modules.sysfile.utils;

import cn.hutool.core.util.StrUtil;
import com.aiurt.common.exception.AiurtBootException;

import java.util.regex.Pattern;

/**
 * @author:wgp
 * @create: 2023-05-25 15:30
 * @Description:
 */
public class FileNameUtils {

    /**
     * 校验文件夹名称是否包含特殊字符
     *
     * @param folderName 文件夹名称
     * @return true表示文件夹名称包含特殊字符，false表示文件夹名称不包含特殊字符
     */
    public static void validateFolderName(String folderName) {
        String regex = ".*[!@#$%^&*()+\\-=\\[\\]{};':\"\\\\|,.<>/?].*";
        boolean result = Pattern.matches(regex, folderName);
        if (result) {
            throw new AiurtBootException("名称不得包含" + regex);
        }
    }
}
