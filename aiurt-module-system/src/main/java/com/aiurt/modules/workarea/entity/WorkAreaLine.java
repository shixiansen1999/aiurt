package com.aiurt.modules.workarea.entity;

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
 * @Description: work_area_line
 * @Author: aiurt
 * @Date:   2022-08-11
 * @Version: V1.0
 */
@Data
@TableName("work_area_line")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="work_area_line对象", description="work_area_line")
public class WorkAreaLine implements Serializable {
    private static final long serialVersionUID = 1L;

	/**工区线路关联表主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "工区线路关联表主键")
    private java.lang.String id;
	/**工区编号*/
	@Excel(name = "工区编号", width = 15)
    @ApiModelProperty(value = "工区编号")
    private java.lang.String workAreaCode;
	/**线路编号*/
	@Excel(name = "线路编号", width = 15)
    @ApiModelProperty(value = "线路编号")
    private java.lang.String lineCode;
	/**删除状态： 0未删除 1已删除*/
	@Excel(name = "删除状态： 0未删除 1已删除", width = 15)
    @ApiModelProperty(value = "删除状态： 0未删除 1已删除")
    private java.lang.Integer delFlag;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private java.util.Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private java.lang.String updateBy;
	/**更新时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
    private java.util.Date updateTime;
}
