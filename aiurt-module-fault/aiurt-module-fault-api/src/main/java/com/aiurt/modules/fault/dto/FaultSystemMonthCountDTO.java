package com.aiurt.modules.fault.dto;

import com.aiurt.common.aspect.annotation.SystemFilterColumn;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author : sbx
 * @Classname : FaultSystemMonthCountDTO
 * @Description : TODO
 * @Date : 2023/5/6 9:48
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class FaultSystemMonthCountDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "专业子系统编码")
    @SystemFilterColumn
    private String subSystemCode;

    @ApiModelProperty(value = "专业子系统名称")
    private String systemName;

    @ApiModelProperty(value = "子系统维修次数")
    private Long frequency;
}
