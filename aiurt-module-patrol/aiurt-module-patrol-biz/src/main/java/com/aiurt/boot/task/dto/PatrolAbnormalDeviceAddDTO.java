package com.aiurt.boot.task.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author sbx
 * @since 2023/10/18
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class PatrolAbnormalDeviceAddDTO {
    private static final long serialVersionUID = 1L;

    /**巡检单巡检结果表id*/
    @ApiModelProperty(value = "巡检单巡检结果表id")
    @NotNull
    private String resultId;
    /**异常设备code集合*/
    @ApiModelProperty(value = "异常设备code集合")
    private List<String> abnormalDeviceCodeList;

}
