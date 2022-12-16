package com.aiurt.modules.positionwifi.entity;

import com.aiurt.common.aspect.annotation.Dict;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: wiif位置管理
 * @Author: aiurt
 * @Date:   2022-11-15
 * @Version: V1.0
 */
@Data
@TableName("cs_position_wifi")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="cs_position_wifi对象", description="wifi位置管理")
public class CsPositionWifi implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    private Date updateTime;
	/**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private String sysOrgCode;
	/**是否已删除（0：未删除，1已删除）*/
	@Excel(name = "是否已删除（0：未删除，1已删除）", width = 15)
    @ApiModelProperty(value = "是否已删除（0：未删除，1已删除）")
    private Integer delFlag;
	/**WiFi名称*/
	@Excel(name = "WiFi名称", width = 15)
    @ApiModelProperty(value = "WiFi名称")
    private String name;
    /**ssid名称*/
    @Excel(name = "ssid", width = 15)
    @ApiModelProperty(value = "ssid")
    private String ssid;
	/**wifi mac地址*/
	@Excel(name = "wifi mac地址", width = 15)
    @ApiModelProperty(value = "wifi mac地址")
    private String mac;
    /**备注*/
    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private  String  remark;
	/**线路编码*/
	@Excel(name = "线路编码", width = 15)
    @ApiModelProperty(value = "线路编码")
    @Dict(dictTable ="cs_line",dicText = "line_name",dicCode = "line_code")
    private String lineCode;
	/**站点编码*/
	@Excel(name = "站点编码", width = 15)
    @ApiModelProperty(value = "站点编码")
    @Dict(dictTable ="cs_station",dicText = "station_name",dicCode = "station_code")
    private String stationCode;
	/**站点位置编码*/
	@Excel(name = "站点位置编码", width = 15)
    @ApiModelProperty(value = "站点位置编码")
    @Dict(dictTable ="cs_station_position",dicText = "position_name",dicCode = "position_code")
    private String positionCode;

    /**位置*/
    @Excel(name = "位置", width = 15)
    @ApiModelProperty(value = "位置")
    @TableField(exist = false)
    private String position;
}
