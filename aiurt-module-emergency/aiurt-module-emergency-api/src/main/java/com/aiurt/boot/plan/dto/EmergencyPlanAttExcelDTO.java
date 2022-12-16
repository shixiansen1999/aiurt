package com.aiurt.boot.plan.dto;
/**
 * 功能描述
 *
 * @author: qkx
 * @date: 2022/12/14
 * @time: 15:18
 */

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 功能描述
 *
 * @author: qkx
 * @date: 2022-12-14 15:18
 */
@Data
public class EmergencyPlanAttExcelDTO {
    /**主键*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
    /**应急预案id*/
    @ApiModelProperty(value = "应急预案id")
    private String emergencyPlanId;

    /**附件名称*/
    @Excel(name = "附件名称", width = 15,needMerge = true)
    @ApiModelProperty(value = "附件名称")
    private String name;

    /**附件类型*/
    @Excel(name = "附件类型", width = 15,needMerge = true)
    @ApiModelProperty(value = "附件类型")
    private String type;

    /**附件大小*/
    @Excel(name = "附件大小", width = 15,needMerge = true)
    @ApiModelProperty(value = "附件大小")
    private String size;

    /**创建人*/
    @Excel(name = "上传人", width = 15,needMerge = true)
    @ApiModelProperty(value = "创建人")
    private String createBy;

    /**创建日期*/
    @Excel(name = "上传时间", width = 15,needMerge = true)
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建日期")
    private java.util.Date createTime;


}
