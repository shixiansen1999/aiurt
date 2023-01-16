package com.aiurt.boot.materials.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zwl
 */
@Data
public class PatrolStandardDTO {

    /**巡检单id*/
    @TableField(exist = false)
    @ApiModelProperty(value = "巡检单id")
    private java.lang.String id;

    /**巡视标准Id*/
    @TableField(exist = false)
    @ApiModelProperty(value = "巡视标准Id")
    private java.lang.String standardId;

    /**巡视标准编码*/
    @TableField(exist = false)
    @ApiModelProperty(value = "巡视标准编码")
    private java.lang.String standardCode;

    /**巡视标准名称*/
    @TableField(exist = false)
    @ApiModelProperty(value = "巡视标准名称")
    private java.lang.String standardName;
}
