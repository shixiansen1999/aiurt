package com.aiurt.modules.fault.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import com.aiurt.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 备件更换记录
 * @Author: aiurt
 * @Date:   2022-06-28
 * @Version: V1.0
 */
@Data
@TableName("device_change_spare_part")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="device_change_spare_part对象", description="备件更换记录")
public class DeviceChangeSparePart implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键id，自动递增*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id，自动递增")
    private Integer id;

	/**类型:1-检修 2-故障*/
    private Integer type;

	/**故障/检修编号*/
	@Excel(name = "故障/检修编号", width = 15)
    @ApiModelProperty(value = "故障/检修编号")
    private String code;

	/**维修记录id*/
    @ApiModelProperty(value = "维修记录id")
    private Integer repairRecordId;

	/**设备id*/
    @ApiModelProperty(value = "设备编码", required = true)
    private String deviceCode;

    @ApiModelProperty(value = "设备名称")
    @TableField(exist = false)
    private String deviceName;

	/**原备件编号*/
    @ApiModelProperty(value = "原组件编号")
    private String oldSparePartCode;

    @ApiModelProperty(value = "原组件名称")
    @TableField(exist = false)
    private String oldSparePartName;

	/**原备件数量*/
    @ApiModelProperty(value = "原组件数量")
    private Integer oldSparePartNum;

	/**原备件所在班组*/
    @ApiModelProperty(value = "原备件所在班组编码")
    private String oldOrgCode;

    @ApiModelProperty()
    private String oldOrgName;

	/**新备件编号*/
    @ApiModelProperty(value = "新备件编号", required = true)
    private String newSparePartCode;

	/**新备件数量*/
    @ApiModelProperty(value = "新备件数量")
    private Integer newSparePartNum;

	/**新备件所在班组*/
    private String newOrgCode;

	/**删除状态：0.未删除 1已删除*/
    @ApiModelProperty(value = "删除状态：0.未删除 1已删除")
    private Integer delFlag;

	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;

	/**修改人*/
    @ApiModelProperty(value = "修改人")
    private String updateBy;

	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
	/**修改时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

	/**是否易耗品(1是,0否)*/
    private String consumables;
}
