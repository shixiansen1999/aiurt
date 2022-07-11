package com.aiurt.boot.plan.req;

import com.aiurt.boot.plan.dto.StationDTO;
import com.aiurt.common.aspect.annotation.Dict;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

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
public class RepairPoolReq {

    @ApiModelProperty(value = "主键id",required = false)
    private java.lang.String id;

    @ApiModelProperty(value = "检修计划单号",required = false)
    private java.lang.String code;

    @ApiModelProperty(value = "检修周期类型：0周检、1月检、2双月检、3季检、4半年检、5年检", required = true)
    @NotNull(message = "请选择检修周期类型")
    private Integer type;

    @ApiModelProperty(value = "是否需要审核：0否 1是", required = true)
    @Dict(dicCode = "inspection_is_confirm")
    @NotNull(message = "请选择是否需要审核")
    private Integer isConfirm;

    @ApiModelProperty(value = "是否需要验收：0否 1是", required = true)
    @Dict(dicCode = "inspection_is_confirm")
    @NotNull(message = "请选择是否需要验收")
    private Integer isReceipt;

    @ApiModelProperty(value = "是否委外：0否1是", required = true)
    @Dict(dicCode = "inspection_is_manual")
    @NotNull(message = "请选择是否委外")
    private Integer isOutsource;

    @ApiModelProperty(value = "作业类型", required = true)
    @Dict(dicCode = "work_type")
    @NotNull(message = "请选择作业类型")
    private Integer workType;

    @ApiModelProperty(value = "是否是手工下发任务，0否1是", required = true)
    private Integer isManual;

    @ApiModelProperty(value = "使用站点code",required = true)
    @TableField(exist = false)
    private List<StationDTO> addStationCode;
    @ApiModelProperty(value = "组织机构code", required = true)
    @TableField(exist = false)
    private List<String> orgCodes;
    @ApiModelProperty(value = "检修标准信息", required = true)
    @TableField(exist = true)
    private List<RepairPoolCodeReq> repairPoolCodes;
}
