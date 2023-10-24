package com.aiurt.boot.task.entity;

import com.aiurt.common.system.base.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 巡检结果异常设备关联表
 * @author sbx
 * @since 2023/10/17
 */
@Data
@TableName("patrol_abnormal_device")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="patrol_abnormal_device对象", description="patrol_abnormal_device")
public class PatrolAbnormalDevice extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**巡检单巡检结果表id，关联patrol_check_result的id*/
    @ApiModelProperty(value = "巡检单巡检结果表id，关联patrol_check_result的id")
    private String resultId;
    /**异常设备code*/
    @ApiModelProperty(value = "异常设备code")
    private String deviceCode;
}
