package com.aiurt.modules.faultcausesolution.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;

/**
 * @author
 * @Description 故障原因解决方案占比数量DTO对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "故障原因解决方案占比数量DTO对象", description = "故障原因解决方案占比数量DTO对象")
public class FaultCauseProportionNumDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 原因出现率百分比
     */
    @Excel(name = "原因出现率百分比", width = 15)
    @ApiModelProperty(value = "原因出现率百分比")
    private String happenRate;
    /**
     * 原因占比数量
     */
    @Excel(name = "原因占比数量", width = 15)
    @ApiModelProperty(value = "原因占比数量")
    private Integer causeNum;
}
