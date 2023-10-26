package com.aiurt.modules.sparepart.entity.dto;

import com.aiurt.modules.fault.dto.SparePartStockDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 故障备件更换DTO
 *
 * @author 李康杰
 * @date 2023-10-14 14:14:29
 */
@Data
public class SpareChangeDTO extends FaultSpareChangeDTO {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "申领物资")
    private List<SparePartStockDTO> dtoList;


}
