package com.aiurt.modules.fault.dto;

import com.aiurt.modules.faultanalysisreport.dto.FaultDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 功能描述
 *
 * @author: qkx
 * @date: 2022-09-15 14:28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class FaultLevelDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "故障超时等级")
    private String  level;

    @ApiModelProperty(value = "数量")
    private Integer faultNumber;

    @ApiModelProperty(value = "故障集合")
    private List<FaultTimeoutLevelDTO> FaultLevelList;



}
