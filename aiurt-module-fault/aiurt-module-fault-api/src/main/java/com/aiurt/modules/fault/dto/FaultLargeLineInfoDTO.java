package com.aiurt.modules.fault.dto;

import com.aiurt.common.aspect.annotation.Dict;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 故障概况
 *
 * @author: qkx
 * @date: 2022-09-06 12:27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class FaultLargeLineInfoDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "线路编码", required = true)
    @Dict(dictTable = "cs_line", dicText = "line_name", dicCode = "line_code")
    private String lineCode;
    @ApiModelProperty(value = "线路名称")
    private String lineName;
    @ApiModelProperty(value = "故障总数")
    private Long sum;
    @ApiModelProperty(value = "已解决数")
    private Integer solve;
    @ApiModelProperty(value = "挂起数")
    private Integer hang;
    @ApiModelProperty(value = "解决率")
    private String solveRate;
}
