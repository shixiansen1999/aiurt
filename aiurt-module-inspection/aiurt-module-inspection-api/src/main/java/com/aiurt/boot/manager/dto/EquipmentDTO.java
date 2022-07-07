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
public class EquipmentDTO {

    /**设备类型编码*/
    @ApiModelProperty(value = "设备类型编码")
    @TableField(exist = false)
    private String deviceTypeCode;

    /**设备类型名称*/
    @ApiModelProperty(value = "设备类型名称")
    @TableField(exist = false)
    private String deviceTypeName;
}
