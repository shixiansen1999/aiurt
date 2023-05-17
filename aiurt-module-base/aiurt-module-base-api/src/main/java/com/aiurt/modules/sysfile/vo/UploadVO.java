package com.aiurt.modules.sysfile.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @description: TypeNameVO
 * @author: Mr.zhao
 * @date: 2021/11/23 16:13
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class UploadVO implements Serializable {

    private String userId;

    private Integer uploadTag;



    /**
     * 查看状态标记
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "查看状态标记")
    private String lookStatusMark;

    /**
     * 编辑状态标记
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "编辑状态标记")
    private String editStatusMark;

    /**
     * 上传状态标记
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "上传状态标记")
    private String uploadStatusMark;


    /**
     * 下载状态标记
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "下载状态标记")
    private String downloadStatusMark;


    /**
     * 删除状态标记
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "删除状态标记")
    private String deleteStatusMark;

    /**
     * 重命名状态标记
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "重命名状态标记")
    private String renameStatusMark;


    /**
     * 在线编辑状态标记
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "在线编辑状态标记")
    private String onlineEditingMark;
}
