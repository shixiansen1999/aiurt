package com.aiurt.boot.plan.dto;

import com.aiurt.boot.plan.entity.RepairPool;
import com.aiurt.boot.plan.entity.RepairPoolCode;
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
public class RepairPoolDTO extends RepairPool {
    @ApiModelProperty("站点code")
    private List<String> stationCodes;
    @ApiModelProperty("组织机构code")
    private List<String> orgCodes;
    @ApiModelProperty("检修标准信息")
    private List<RepairPoolCode> repairPoolCodes;
}
