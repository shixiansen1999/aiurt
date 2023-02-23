package com.aiurt.boot.task.dto;


import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @projectName: aiurt-platform
 * @package: com.aiurt.boot.plan.dto
 * @className: SystemInformationDTO
 * @author: life-0
 * @date: 2023/2/23 10:27
 * @description: TODO
 * @version: 1.0
 */

@Data
public class SystemInformationDTO {


    @ApiModelProperty(value = "线路Code")
    @TableField(exist = false)
    private String lineCode;


    @ApiModelProperty(value = "线路名称")
    @TableField(exist = false)
    private String lineName;


    @ApiModelProperty(value = "站点Code")
    @TableField(exist = false)
    private String siteCode;


    @ApiModelProperty(value = "站点名称")
    @TableField(exist = false)
    private java.lang.String siteName;


    @ApiModelProperty(value = "系统名称")
    @TableField(exist = false)
    private java.lang.String systemTyp;


    @ApiModelProperty(value = "计划修完成数量")
    @TableField(exist = false)
    private java.lang.Long iplanComplete;


    @ApiModelProperty(value = "计划修总数")
    @TableField(exist = false)
    private java.lang.Long iplanSum;


    @ApiModelProperty(value = "故障修完成数量")
    @TableField(exist = false)
    private java.lang.Long faultComplete;


    @ApiModelProperty(value = "故障修总数")
    @TableField(exist = false)
    private java.lang.Long faultSum;


    @ApiModelProperty(required = true)
    private Integer pageNo=1;

    @ApiModelProperty(required = true)
    private Integer pageSize=10;
}
