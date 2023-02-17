package com.aiurt.modules.usageconfig.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zwl
 */
@Data
public class BusinessDataStatisticsDTO {

    @TableField(exist = false)
    @ApiModelProperty(value = "统计主键id")
    private String configId;

    @TableField(exist = false)
    @ApiModelProperty(value = "统计表名称")
    private String nodeName;

    @TableField(exist = false)
    @ApiModelProperty(value = "统计表名")
    private String tableName;

    @TableField(exist = false)
    @ApiModelProperty(value = "父级节点")
    private String pid;

    @TableField(exist = false)
    @ApiModelProperty(value = "是否有子节点")
    private String hasChild;

    @TableField(exist = false)
    @ApiModelProperty(value = "状态（1启用/0禁用）")
    private Integer state;

    @TableField(exist = false)
    @ApiModelProperty(value = "总数")
    private Integer total;

    @TableField(exist = false)
    @ApiModelProperty(value = "新增数")
    private Integer newlyAdded;
}
