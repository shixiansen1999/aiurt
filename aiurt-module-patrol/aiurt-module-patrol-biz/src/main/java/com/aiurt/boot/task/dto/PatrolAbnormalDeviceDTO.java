package com.aiurt.boot.task.dto;

import com.aiurt.common.system.base.entity.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author sbx
 * @since 2023/10/20
 */
@Data
public class PatrolAbnormalDeviceDTO extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**巡检单巡检结果表id，关联patrol_check_result的id*/
    @ApiModelProperty(value = "巡检单巡检结果表id，关联patrol_check_result的id")
    private String resultId;
    /**异常设备code*/
    @ApiModelProperty(value = "异常设备code")
    private String deviceCode;

    /**异常设备名称*/
    @ApiModelProperty(value = "异常设备名称")
    private String deviceName;
}
