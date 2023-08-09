package com.aiurt.modules.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * GPS、wifi定位数据上报
 * @author hlq
 */
@Data
@TableName("sys_user_position_current")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "sys_user_position_current对象", description = "用户连接站点wifi的当前位置表")
public class SysUserPositionCurrent {

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
    private String id;
    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    private String createBy;
    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    /**
     * 修改人
     */
    @ApiModelProperty(value = "修改人")
    private String updateBy;
    /**
     * 修改时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
    private Date updateTime;
    /**
     * 当前登录人所属部门
     */
    @ApiModelProperty(value = "当前登录人所属部门")
    private String sysOrgCode;
    /**
     * SSID（WiFi标识）
     */
    @ApiModelProperty(value = "SSID（WiFi标识）")
    private String ssid;
    /**
     * bssid（WiFi mac地址）
     */
    @ApiModelProperty(value = "bssid（WiFi mac地址）")
    private String bssid;
    /**
     * 经度
     */
    @ApiModelProperty(value = "经度")
    private BigDecimal longitude;
    /**
     * 纬度
     */
    @ApiModelProperty(value = "纬度")
    private BigDecimal latitude;
    /**
     * 上传时间
     */
    @ApiModelProperty(value = "上传时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date uploadTime;
    /**
     * 当前用户(app)所在的车站编号，为null值时站点code更新为null，并设置为异常
     */
    @ApiModelProperty(value = "当前用户(app)所在的车站编号")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String stationCode;
    /**
     * 用户所在的上一个车站的连接wifi的mac地址，null值也更新
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "用户所在的上一个车站的连接wifi的mac地址")
    private String lastBssid;
    /**
     * 用户所在的上一个车站的连接时间
     */
    @ApiModelProperty(value = "用户所在的上一个车站的连接时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastUploadTime;
    /**
     * 当用户所在的上一个车站编号
     */
    @ApiModelProperty(value = "用户所在的上一个车站编号")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String lastStationCode;
    /**
     * 当前用户(app)所在的位置是否异常，1-异常，0-正常
     */
    @ApiModelProperty(value = "当前用户(app)所在的位置是否异常，1-异常，0-正常")
    private String isPositionError;
}
