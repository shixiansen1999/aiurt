package com.aiurt.modules.fault.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author sbx
 * @Classname :  FaultMonthCountDTO
 * @Description : TODO
 * @Date :2023/5/6 9:40
 * @Created by   : sbx
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class FaultMonthCountDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "次数")
    private Integer sum;

    @ApiModelProperty(value = "月份")
    private String month;

    @ApiModelProperty(value = "子系统月份故障次数")
    private List<FaultSystemMonthCountDTO> faultSystemMonthCountDTOList;
}
