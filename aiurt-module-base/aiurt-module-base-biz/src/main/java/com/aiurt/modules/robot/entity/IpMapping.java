package com.aiurt.modules.robot.entity;

import com.aiurt.common.aspect.annotation.Dict;
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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 * @Description: ip_mapping
 * @Author: jeecg-boot
 * @Date:   2022-10-10
 * @Version: V1.0
 */
@Data
@TableName("ip_mapping")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="ip_mapping对象", description="ip_mapping")
public class IpMapping implements Serializable {
    private static final long serialVersionUID = 1L;

	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
    private String id;
	/**内网ip地址*/
	@Excel(name = "内网ip地址", width = 15)
    @ApiModelProperty(value = "内网ip地址")
    @Size(max = 32, message = "内网ip最大长度为32位")
    @NotBlank(message = "内网ip地址不能为空")
    private String insideIp;
	/**外网ip地址*/
	@Excel(name = "外网ip地址", width = 15)
    @ApiModelProperty(value = "外网ip地址")
    @Size(max = 32, message = "外网ip最大长度为32位")
    @NotBlank(message = "外网ip地址不能为空")
    private String outsideIp;
	/**是否映射*/
	@Excel(name = "是否映射", width = 15)
    @ApiModelProperty(value = "是否映射")
    @Dict(dicCode = "is_ip_mapping")
    private Integer isMapping;
	/**内部端口*/
	@Excel(name = "内部端口", width = 15)
    @ApiModelProperty(value = "内部端口")
    @NotNull(message = "内部端口不能为空")
    @Size(max = 11, message = "内部端口最大长度为11位")
    private Integer insidePort;
	/**外部端口*/
	@Excel(name = "外部端口", width = 15)
    @ApiModelProperty(value = "外部端口")
    @NotNull(message = "外部端口不能为空")
    @Size(max = 11, message = "外部端口最大长度为11位")
    private Integer outsidePort;
	/**描述*/
    @ApiModelProperty(value = "描述")
    @Size(max = 255, message = "描述最大长度为255位")
    private String resume;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
	/**修改人*/
    @ApiModelProperty(value = "修改人")
    private String updateBy;
	/**修改时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "修改时间")
    private Date updateTime;
}
