package com.aiurt.boot.manager.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zwl
 * @Title:
 * @Description:
 * @date 2022/6/2412:15
 */
@Data
public class SubsystemDTO {

    /**子系统编码*/
    @ApiModelProperty(value = "子系统编码")
    @TableField(exist = false)
    private String systemCode;

    /**子系统名称*/
    @ApiModelProperty(value = "子系统名称")
    @TableField(exist = false)
    private String systemName;
}
