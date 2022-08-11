package com.aiurt.modules.workarea.dto;

import com.aiurt.common.aspect.annotation.MajorFilterColumn;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @author cgkj0
 * @version 1.0
 * @date 2022/8/11
 * @desc
 */
@Data
public class SubSystem {
    /**主键id*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private String id;
    /**专业编码*/
    @Excel(name = "子系统编码", width = 15)
    @ApiModelProperty(value = "子系统编码")
    @MajorFilterColumn
    private String systemCode;
    /**专业名称*/
    @Excel(name = "子系统名称", width = 15)
    @ApiModelProperty(value = "子系统名称")
    private String systemName;
}
