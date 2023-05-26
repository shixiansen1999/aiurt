package com.aiurt.boot.task.dto;

import com.aiurt.modules.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * @author : sbx
 * @Classname : TemperatureHumidityDTO
 * @Description : TODO
 * @Date : 2023/5/16 12:28
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class TemperatureHumidityDTO extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private java.lang.String id;
    /**站点ip*/
    @ApiModelProperty(value = "站点ip")
    private java.lang.String ip;
    /**温度*/
    @ApiModelProperty(value = "温度")
    private Float temperature;
    /**湿度*/
    @ApiModelProperty(value = "湿度")
    private Float humidity;
    @ApiModelProperty(value = "线路code")
    private String lineCode;
    @ApiModelProperty(value = "线路名称")
    private String lineName;
    @ApiModelProperty(value = "站点code")
    private String stationCode;
    @ApiModelProperty(value = "站点名称")
    private String stationName;
    /**创建时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private java.util.Date createTime;
    /**更新时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private java.util.Date updateTime;

}

