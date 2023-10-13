package com.aiurt.modules.sparepart.entity.dto;

import com.aiurt.modules.material.entity.MaterialRequisition;
import com.aiurt.modules.sparepart.entity.SparePartStockInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecg.common.system.vo.LoginUser;

import java.io.Serializable;

/**
 * 故障备件更换父DTO
 *
 * @author 李康杰
 * @date 2023-10-14 14:14:29
 */
@Data
public class FaultSpareChangeDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "故障编码")
    private String faultCode;
    @ApiModelProperty(value = "故障维修记录id")
    private String faultRepairRecordId;
    @ApiModelProperty(value = "三级库仓库信息")
    private SparePartStockInfo stockInfo;
    @ApiModelProperty(value = "登录人信息")
    private LoginUser loginUser;
    @ApiModelProperty(value = "领料单")
    private MaterialRequisition requisition;
}
