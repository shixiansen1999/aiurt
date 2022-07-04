package com.aiurt.boot.plan.dto;

import com.aiurt.boot.plan.entity.RepairPoolCode;
import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.modules.basic.entity.DictEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author wgp
 * @Title:
 * @Description:
 * @date 2022/6/2718:42
 */

@Data
@ApiModel(value = "手工下发检修任务", description = "repair_manual_task")
public class RepairPoolDTO extends DictEntity {

    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id", required = false)
    private java.lang.String id;

    @ApiModelProperty(value = "检修周期类型：0周检、1月检、2双月检、3季检、4半年检、5年检", required = true)
    @NotNull(message = "请选择检修周期类型")
    @Dict(dicCode = "inspection_cycle_type")
    private java.lang.Integer type;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "开始时间", required = false)
    private java.util.Date startTime;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "结束时间", required = false)
    private java.util.Date endTime;


    @ApiModelProperty(value = "是否需要审核：0否 1是", required = true)
    @Dict(dicCode = "inspection_is_confirm")
    @NotNull(message = "请选择是否需要审核")
    private java.lang.Integer isConfirm;

    @ApiModelProperty(value = "是否需要验收：0否 1是", required = true)
    @Dict(dicCode = "inspection_is_confirm")
    @NotNull(message = "请选择是否需要验收")
    private java.lang.Integer isReceipt;


    @ApiModelProperty(value = "是否委外：0否1是", required = true)
    @Dict(dicCode = "inspection_is_manual")
    @NotNull(message = "请选择是否委外")
    private java.lang.Integer isOutsource;


    @ApiModelProperty(value = "作业类型", required = true)
    @Dict(dicCode = "work_type")
    @NotNull(message = "请选择作业类型")
    private java.lang.Integer workType;

    @ApiModelProperty(value = "是否是手工下发任务，0否1是", required = true)
    private java.lang.Integer isManual;

    @ApiModelProperty(value = "检修计划单号", required = false)
    private java.lang.String code;

    @ApiModelProperty("使用站点code（回显使用）")
    @TableField(exist = false)
    private List<String> stationCodes;
    @ApiModelProperty("组织机构code")
    @TableField(exist = false)
    private List<String> orgCodes;
    @ApiModelProperty(value = "检修标准信息")
    @TableField(exist = false)
    private List<RepairPoolCode> repairPoolCodes;
}
