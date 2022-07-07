package com.aiurt.boot.task.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "专业对象", description = "专业对象")
public class MajorDTO {

    /**
     * 专业编码
     */
    @ApiModelProperty(value = "专业编码")
    private java.lang.String majorCode;
    /**
     * 专业名称
     */
    @ApiModelProperty(value = "专业名称")
    private java.lang.String majorName;

    /**
     * 专业下的子系统信息
     */
    @ApiModelProperty(value = "专业下的子系统信息")
    private List<SubsystemDTO> subsystemInfo;

}
