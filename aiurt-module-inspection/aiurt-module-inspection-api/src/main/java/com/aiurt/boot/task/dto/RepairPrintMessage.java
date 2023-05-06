package com.aiurt.boot.task.dto;

import com.aiurt.boot.task.entity.RepairTaskResult;
import com.aiurt.common.result.SpareResult;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author lkj
 */
@Data
public class RepairPrintMessage {
    @TableField(exist = false)
    @ApiModelProperty(value = "标题")
    private String title;
    /** 状态名称*/
    @TableField(exist = false)
    @ApiModelProperty(value = "检修结果名称")
    private String repairRecord;
    @TableField(exist = false)
    @ApiModelProperty(value = "处理结果")
    private String repairResult;
    @TableField(exist = false)
    @ApiModelProperty(value = "备件更换")
    private List<SpareResult> spareChange;
    /**检修单附件*/
    @TableField(exist = false)
    @ApiModelProperty(value = "检修单附件")
    private List<String> enclosureUrl;

    @ApiModelProperty(value = "检修单（树形）")
    @TableField(exist = false)
    List<RepairTaskResult> repairTaskResultList;
}
