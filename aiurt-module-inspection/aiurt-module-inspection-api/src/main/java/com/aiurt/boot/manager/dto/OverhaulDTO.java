package com.aiurt.boot.manager.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zwl
 * @Title:
 * @Description:
 * @date 2022/6/2417:52
 */
@Data
public class OverhaulDTO {

    /**检修标准id*/
    @TableField(exist = false)
    @ApiModelProperty(value = "检修标准id")
    private String standardId;

    /**检修标准名称*/
    @TableField(exist = false)
    @ApiModelProperty(value = "检修标准名称")
    private String overhaulStandardName;
}
