package com.aiurt.boot.manager.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author zwl
 * @Title:
 * @Description:
 * @date 2022/6/2417:20
 */
@Data
public class EquipmentOverhaulDTO {

    /**设备列表*/
    @ApiModelProperty(value = "设备列表")
    @TableField(exist = false)
    private List<EquipmentDTO> equipmentDTOList;

    /**标准名称列表*/
    @ApiModelProperty(value = "标准名称列表")
    @TableField(exist = false)
    private List<OverhaulDTO> overhaulDTOList;
}
