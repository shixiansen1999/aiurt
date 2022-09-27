package com.aiurt.modules.robot.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author wgp
 * @Title:
 * @Description: 机器人巡检区域点位DTO
 * @date 2022/9/269:43
 */
@Data
public class AreaPointDTO {
    @ApiModelProperty("id")
    private String id;
    @ApiModelProperty("名称")
    private String name;
    @ApiModelProperty("父节点id")
    private String pid;
    @ApiModelProperty(value = "子节点")
    private List<AreaPointDTO> children;
    @ApiModelProperty("是否是点位0否1是")
    private Integer isPoint;
}
