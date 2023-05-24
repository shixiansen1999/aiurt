package com.aiurt.boot.wificonnect.entity;

import java.io.Serializable;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
 * @Description: wifi_connect
 * @Author: jeecg-boot
 * @Date:   2023-05-24
 * @Version: V1.0
 */
@Data
@TableName("wifi_connect")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="wifi_connect对象", description="wifi_connect")
public class WifiConnect implements Serializable {
    private static final long serialVersionUID = 1L;

	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
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
    /**删除标志*/
    @Excel(name = "删除标志", width = 15)
    @ApiModelProperty(value = "删除标志")
    private Integer delFlag;
	/**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private String sysOrgCode;
	/**SSID（WiFi标识）*/
	@Excel(name = "SSID（WiFi标识）", width = 15)
    @ApiModelProperty(value = "SSID（WiFi标识）")
    private String ssid;
	/**bssid（WiFi 的mac地址）*/
	@Excel(name = "bssid（WiFi 的mac地址）", width = 15)
    @ApiModelProperty(value = "bssid（WiFi 的mac地址）")
    private String bssid;
	/**连接时间*/
	@Excel(name = "连接时间", width = 15, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "连接时间")
    private Date connectTime;
	/**线路code*/
    @NotNull(message = "线路code不能为空")
	@Excel(name = "线路code", width = 15)
    @ApiModelProperty(value = "线路code")
    private String lineCode;
	/**站点code*/
	@NotNull(message = "站点code不能为空")
	@Excel(name = "站点code", width = 15)
    @ApiModelProperty(value = "站点code")
    private String stationCode;
}
