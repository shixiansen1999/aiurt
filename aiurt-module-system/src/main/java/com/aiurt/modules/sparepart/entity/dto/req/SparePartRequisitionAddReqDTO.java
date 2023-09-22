package com.aiurt.modules.sparepart.entity.dto.req;

import com.aiurt.modules.sparepart.entity.dto.SparePartRequisitionDetailDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.List;

/**
 * 三级库申领的添加、编辑等请求DTO
 *
 * @author 华宜威
 * @date 2023-09-21 10:22:29
 */
@Data
public class SparePartRequisitionAddReqDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**申领单id*/
    @ApiModelProperty(value = "申领单id")
    private String id;

    /**计划领用时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "计划领用时间")
    private java.util.Date planApplyTime;

    /**领用线路编码*/
    @ApiModelProperty(value = "领用线路编码")
    private String applyLineCode;

    /**保管仓库编号*/
    @ApiModelProperty(value = "保管仓库编号")
    private String custodialWarehouseCode;

    /**用途*/
    @ApiModelProperty(value = "用途")
    private String useTo;

    /**物资清单*/
    @ApiModelProperty(value = "物资清单")
    List<SparePartRequisitionDetailDTO> sparePartRequisitionDetailDTOS;

}
