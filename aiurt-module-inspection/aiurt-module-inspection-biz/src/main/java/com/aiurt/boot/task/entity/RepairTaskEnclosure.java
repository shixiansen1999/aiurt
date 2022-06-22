package com.aiurt.boot.task.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
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
 * @Description: repair_task_enclosure
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
@Data
@TableName("repair_task_enclosure")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="repair_task_enclosure对象", description="repair_task_enclosure")
public class RepairTaskEnclosure implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private java.lang.Integer id;
	/**关联repair_task_device_rel表的id*/
	@Excel(name = "关联repair_task_device_rel表的id", width = 15)
    @ApiModelProperty(value = "关联repair_task_device_rel表的id")
    private java.lang.Integer taskDeviceRelId;
	/**url地址*/
	@Excel(name = "url地址", width = 15)
    @ApiModelProperty(value = "url地址")
    private java.lang.String url;
	/**删除状态：0.未删除 1已删除*/
	@Excel(name = "删除状态：0.未删除 1已删除", width = 15)
    @ApiModelProperty(value = "删除状态：0.未删除 1已删除")
    private java.lang.Integer delFlag;
	/**创建时间，CURRENT_TIMESTAMP*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间，CURRENT_TIMESTAMP")
    private java.util.Date createTime;
	/**修改时间，根据当前时间戳更新*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "修改时间，根据当前时间戳更新")
    private java.util.Date updateTime;
	/**创建者*/
    @ApiModelProperty(value = "创建者")
    private java.lang.String createBy;
	/**更新者*/
    @ApiModelProperty(value = "更新者")
    private java.lang.String updateBy;
}
