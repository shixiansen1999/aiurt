package com.aiurt.boot.manager.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author zwl
 * @Title:
 * @Description:
 * @date 2022/8/117:16
 */
@Data
public class FaultCallbackDTO {

    /**检修单id*/
    @TableField(exist = false)
    @ApiModelProperty(value = "检修单id",required = true)
    @NotBlank
    private String deviceId;


    /**回调故障编码*/
    @TableField(exist = false)
    @ApiModelProperty(value = "回调故障编码",required = true)
    @NotNull
    private String faultCode;
}
