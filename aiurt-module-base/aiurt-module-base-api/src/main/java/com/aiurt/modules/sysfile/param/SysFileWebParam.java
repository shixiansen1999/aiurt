package com.aiurt.modules.sysfile.param;

import com.aiurt.modules.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @description: SysFileWebParam
 * @author: Mr.zhao
 * @date: 2021/11/23 14:05
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class SysFileWebParam extends BaseEntity implements Serializable {

    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    private String createByName;

    /**
     * 文件名称
     */
    @ApiModelProperty(value = "文件名称")
    private String name;
    /**
     * 类型id
     */
    @ApiModelProperty(value = "类型id")
    private Long typeId;
    /**
     * 文件夹code层级编码
     */
    @ApiModelProperty(value = "文件夹code层级编码")
    private String folderCodeCc;
    /**
     * 文档格式
     */
    @ApiModelProperty(value = "文档格式")
    private String type;
    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private LocalDate startTime;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private LocalDate endTime;
}
