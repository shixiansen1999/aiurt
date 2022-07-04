package com.aiurt.modules.fault.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 组件更换记录
 * @Author: aiurt
 * @Date:   2022-06-28
 * @Version: V1.0
 */
@Data
@TableName("device_change_spare_part")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="device_change_spare_part对象", description="组件更换记录")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeviceChangeSparePart implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键id，自动递增*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id，自动递增")
    private String id;

	/**类型:1-检修 2-故障*/
    private Integer type;

	/**故障/检修编号*/
	@Excel(name = "故障/检修编号", width = 15)
    @ApiModelProperty(value = "故障/检修编号")
    private String code;

	/**维修记录id*/
    @ApiModelProperty(value = "维修记录id")
    private String repairRecordId;

	/**设组id*/
    @ApiModelProperty(value = "设组编码", required = true)
    private String deviceCode;

    @ApiModelProperty(value = "设组名称")
    @TableField(exist = false)
    private String deviceName;

	/**原组件编号*/
    @ApiModelProperty(value = "原组件编号")
    private String oldSparePartCode;

    @ApiModelProperty(value = "原组件名称")
    @TableField(exist = false)
    private String oldSparePartName;

	/**原组件数量*/
    @ApiModelProperty(value = "原组件数量")
    private Integer oldSparePartNum;

	/**原组件所在班组*/
    @ApiModelProperty(value = "原组件所在班组编码")
    private String oldOrgCode;

    @ApiModelProperty("机构名称")
    private String oldOrgName;

	/**新组件编号*/
    @ApiModelProperty(value = "新组件编号", required = true)
    private String newSparePartCode;

    @ApiModelProperty(value = "新组件名称", required = true)
    private String newSparePartName;

	/**新组件数量*/
    @ApiModelProperty(value = "新组件数量")
    private Integer newSparePartNum;

	/**新组件所在班组*/
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
	@ApiModelProperty(value = "是否易耗品(1是,0否)")
    private String consumables;
}
