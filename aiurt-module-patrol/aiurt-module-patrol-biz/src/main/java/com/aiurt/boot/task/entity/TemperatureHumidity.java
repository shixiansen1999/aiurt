package com.aiurt.boot.task.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@TableName("temperature_humidity")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="temperature_humidity对象", description="temperature_humidity")
public class TemperatureHumidity {

    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private java.lang.String id;
    /**站点ip*/
    @Excel(name = "站点ip", width = 15)
    @ApiModelProperty(value = "站点ip")
    private java.lang.String ip;
    /**温度*/
    @Excel(name = "温度", width = 15)
    @ApiModelProperty(value = "温度")
    private float temperature;
    /**湿度*/
    @Excel(name = "湿度", width = 15)
    @ApiModelProperty(value = "湿度")
    private float humidity;

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
