package com.aiurt.modules.personnelportrait.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author
 * @description
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "任务次数", description = "任务次数")
public class WaveResDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 年份
     */
    @ApiModelProperty(value = "年份")
    private List<Integer> year;

    /**
     * 巡视数
     */
    @ApiModelProperty(value = "巡视数")
    private List<Long> patrol;

    /**
     * 检修数
     */
    @ApiModelProperty(value = "检修数")
    private List<Long> inspection;

    /**
     * 故障数
     */
    @ApiModelProperty(value = "故障数")
    private List<Long> fault;
}
