package com.aiurt.modules.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @projectName: aiurt-platform
 * @package: com.aiurt.modules.common.entity
 * @className: SelectDeviceType
 * @author: life-0
 * @date: 2023/2/7 10:37
 * @description: TODO
 * @version: 1.0
 */
@Data
@ApiModel("设备分类下拉列表")
@AllArgsConstructor
public class SelectDeviceType {
    /**主键id*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private String id;
    @JsonProperty(value = "pId")
    private String pid;
    private String value;

    private String title;
    @JsonProperty(value = "isLeaf")
    private boolean Leaf;

    private boolean selectable;
}
