package com.aiurt.modules.sparepart.entity.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import liquibase.change.core.LoadDataChange;
import lombok.Data;

/**
 * @Author zwl
 * @Date 2022/10/18
 * @Version 1.0
 */
@Data
public class SparePartConsume {

    @ApiModelProperty(value = "月份")
    @TableField(exist = false)
    private String month;

    @ApiModelProperty(value = "季度")
    @TableField(exist = false)
    private String quarter;

    @ApiModelProperty(value = "个数")
    @TableField(exist = false)
    private Long count;


}
