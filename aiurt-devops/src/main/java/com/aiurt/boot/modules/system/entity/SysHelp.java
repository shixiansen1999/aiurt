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
 * @Description: 帮助
 * @Author: swsc
 * @Date:   2021-12-01
 * @Version: V1.0
 */
@Data
@TableName("sys_help")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="sys_help对象", description="帮助")
public class SysHelp {

	/**id*/
	@TableId(type= IdType.ASSIGN_UUID)
    @ApiModelProperty(value = "id")
	public Integer id;
	/**标题*/
	@Excel(name = "标题", width = 15)
    @ApiModelProperty(value = "标题")
	public String title;
	/**内容*/
	@Excel(name = "内容", width = 15)
    @ApiModelProperty(value = "内容")
	public Object content;
	/**状态（0->未发布;1->发布）*/
	@Excel(name = "状态（0->未发布;1->发布）", width = 15)
    @ApiModelProperty(value = "状态（0->未发布;1->发布）")
	public Integer status;
	/**删除状态（0->正常;1->已删除）*/
	@Excel(name = "删除状态（0->正常;1->已删除）", width = 15)
    @ApiModelProperty(value = "删除状态（0->正常;1->已删除）")
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
