package com.aiurt.modules.fault.dto;

import com.aiurt.common.aspect.annotation.SystemFilterColumn;
import com.aiurt.modules.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @author : sbx
 * @Classname : FaultSystemDTO
 * @Description : TODO
 * @Date : 2023/8/23 12:11
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class FaultSystemDTO extends BaseEntity implements Serializable {

    @ApiModelProperty(value = "id")
    private String id;
    @ApiModelProperty(value = "故障编码")
    private String code;
    @ApiModelProperty(value = "发生时间")
    private Date happenTime;
    @ApiModelProperty(value = "故障报修时长")
    private Long duration;
    @ApiModelProperty(value = "线路编码")
    private String lineCode;
    @ApiModelProperty(value = "专业子系统编码")
    @SystemFilterColumn
    private String subSystemCode;
    @ApiModelProperty(value = "专业子系统名称")
    private String systemName;
}
