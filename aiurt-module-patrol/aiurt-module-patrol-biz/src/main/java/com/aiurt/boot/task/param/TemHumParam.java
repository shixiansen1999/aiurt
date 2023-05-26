package com.aiurt.boot.task.param;

import com.aiurt.modules.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @author : sbx
 * @Classname : TemHumParam
 * @Description : TODO
 * @Date : 2023/5/25 17:21
 */

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class TemHumParam extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "查询模式：0获取当前整点时刻的温湿度，1获取当天(每个整点时刻)，2获取近一周（每天14点整），3获取近30天（每天14点整）")
    @NotNull(message = "查询模式不能为空")
    private Integer mode;

    @ApiModelProperty(value = "整点时刻")
    private String hour;

    @ApiModelProperty(value = "当前时间")
    private Date date;

    @ApiModelProperty(value = "时间间隔")
    private Integer interval;

    @NotNull(message = "线路code不能为空")
    @ApiModelProperty(value = "线路code")
    private String lineCode;

    @NotNull(message = "站点code不能为空")
    @ApiModelProperty(value = "站点code")
    private String stationCode;
}
