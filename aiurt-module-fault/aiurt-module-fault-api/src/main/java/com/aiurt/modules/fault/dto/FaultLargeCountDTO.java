package com.aiurt.modules.fault.dto;

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
 * 功能描述
 *
 * @author: qkx
 * @date: 2022-09-13 15:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class FaultLargeCountDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 总故障数
     */
    @ApiModelProperty(value = "总故障数")
    private Integer sum;

    /**
     * 未解决故障数
     */
    @ApiModelProperty(value = "未解决故障数")
    private Integer unSolve;

    /**
     * 当日新增数
     */
    @ApiModelProperty(value = "当日新增数")
    private Integer newAddNumber;

    /**
     * 当日已解决数
     */
    @ApiModelProperty(value = "当日已解决数")
    private Integer solve;

}
