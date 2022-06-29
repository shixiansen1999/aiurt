package com.aiurt.boot.plan.dto;

import com.aiurt.boot.plan.entity.RepairPool;
import com.aiurt.boot.plan.entity.RepairPoolCode;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author wgp
 * @Title:
 * @Description:
 * @date 2022/6/2718:42
 */

@Data
@ApiModel(value = "手工下发检修任务", description = "repair_manual_task")
public class RepairPoolDTO extends RepairPool {
    @ApiModelProperty("使用站点code（添加使用）")
    @TableField(exist = false)
    private List<StationDTO> addStationCode;
    @ApiModelProperty("使用站点code（回显使用）")
    @TableField(exist = false)
    private List<String> stationCodes;
    @ApiModelProperty("组织机构code")
    @TableField(exist = false)
    private List<String> orgCodes;
    @ApiModelProperty("检修标准信息")
    @TableField(exist = false)
    private List<RepairPoolCode> repairPoolCodes ;
}
