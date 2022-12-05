package com.aiurt.boot.materials.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


/**
 * @author zwl
 */
@Data
public class MaterialPatrolDTO {

    /**应急物资巡视编码*/
    @TableField(exist = false)
    @ApiModelProperty(value = "应急物资巡视编码")
    private java.lang.String materialsPatrolCode;


    /**巡视人*/
    @TableField(exist = false)
    @ApiModelProperty(value = "巡视人")
    private java.lang.String patrolName;


    /**巡检标准下拉列表*/
    @TableField(exist = false)
    @ApiModelProperty(value = "巡检标准下拉列表")
    private List<PatrolStandardDTO> patrolStandardDTOList;
}
