package com.aiurt.boot.screen.model;

import com.aiurt.boot.task.dto.TemperatureHumidityDTO;
import com.aiurt.modules.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author : sbx
 * @Classname : 大屏温湿度
 * @Description : TODO
 * @Date : 2023/5/26 17:55
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class ScreenTemHum extends BaseEntity implements Serializable {
    /**
     * 当前整点时刻的温湿度
     */
    @ApiModelProperty(value = "当前整点时刻的温湿度")
    private TemperatureHumidityDTO currentTemHum;
    /**
     * 温湿度集合
     */
    @ApiModelProperty(value = "温湿度集合")
    private List<TemperatureHumidityDTO> temHumList;
}
