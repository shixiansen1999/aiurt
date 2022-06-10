package com.aiurt.boot.modules.fastdfs.model;

import lombok.Data;

/**
 * file实体类
 *
 */
@Data
public class UploadFile  {
    private static final long serialVersionUID = -1438078028040922174L;
    /**
     * 原始文件名
     */
    private String name;
    /**
     * 是否图片
     */
    private Boolean isImg;
    /**
     * 上传文件类型
     */
    private String contentType;
    /**
     * 文件大小
     */
    private long size;

    private byte[] bytes;

}
