package com.aiurt.boot.modules.system.entity;

import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 关于
 * @Author: swsc
 * @Date:   2021-12-01
 * @Version: V1.0
 */
@Data
@TableName("sys_about")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="sys_about对象", description="关于")
public class SysAbout {

	/**id*/
	@TableId(type= IdType.UUID)
    @ApiModelProperty(value = "id")
	public Integer id;
	/**内容*/
	@Excel(name = "内容", width = 15)
    @ApiModelProperty(value = "内容")
	public Object content;
	/**状态 0禁用 1启用*/
	@Excel(name = "状态 0禁用 1启用", width = 15)
    @ApiModelProperty(value = "状态 0禁用 1启用")
	public Integer status;
	/**删除标志 */
	@Excel(name = "删除标志 ", width = 15)
    @ApiModelProperty(value = "删除标志 ")
	public Integer delFlag;
	/**创建时间*/
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	public Date createTime;
	/**更新时间*/
	@Excel(name = "更新时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
	public Date updateTime;
}
