package com.aiurt.modules.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
    private String pid;
    private String value;

    private String title;

    private boolean isLeaf;

    private boolean selectTable;
}
