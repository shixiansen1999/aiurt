package com.aiurt.modules.sysfile.utils;

import com.aiurt.common.exception.AiurtBootException;

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
     * @return true表示文件夹名称不包含特殊字符，false表示文件夹名称包含特殊字符
     */
    public static void validateFolderName(String folderName) {
        // 定义特殊字符的正则表达式
        String specialChars = "[!@#$%^&*()_+\\[\\]{};':\"|<>?/.,]";

        // 使用正则表达式匹配文件夹名称
        boolean hasSpecialChars = folderName.matches("." + specialChars + ".");
        if (!hasSpecialChars) {
            throw new AiurtBootException("名称不得包含" + specialChars);
        }
    }
}
