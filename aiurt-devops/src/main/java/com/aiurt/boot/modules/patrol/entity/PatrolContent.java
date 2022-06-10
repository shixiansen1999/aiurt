package com.aiurt.boot.modules.patrol.entity;

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

import java.util.Date;

/**
 * @Description: 巡检项
 * @Author: Mr.zhao
 * @Date: 2021-09-15
 * @Version: V1.0
 */
@Data
@TableName("t_patrol_content")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "patrol_content对象", description = "巡检项")
public class PatrolContent {

	/**
	 * 主键id		自动递增
	 */
	@TableId(type = IdType.AUTO)
	@ApiModelProperty(value = "主键id		自动递增")
	private Long id;

	/**
	 * 巡检规范id		patrol.id
	 */
	@Excel(name = "巡检规范编号(勿改)", width = 15)
	@ApiModelProperty(value = "巡检规范id		patrol.id")
	private Long recordId;

	/**
	 * 父级id		patrol_content.id 顶级为0
	 */
	@Excel(name = "父级id", width = 15)
	@ApiModelProperty(value = "父级id		patrol_content.id 顶级为0")
	private Long parentId;

	/**
	 * 检查项类型		否0 是 1
	 */
	@Excel(name = "检查项类型", width = 15 ,replace = {"否_0","是_1"})
	@ApiModelProperty(value = "检查项类型		否0 是 1")
	private Integer type;

	/**
	 * 显示顺序		排序字段.需查重处理
	 */
	@Excel(name = "显示顺序", width = 15)
	@ApiModelProperty(value = "显示顺序		排序字段.需查重处理")
	private Integer sequence;

	/**
	 * 填写选择状态项		0,选择项 1.文字填充项
	 */
	@Excel(name = "填写选择状态项", width = 15,replace = {"选择项_0", "文字填充项_1"})
	@ApiModelProperty(value = "填写选择状态项		0,选择项 1.文字填充项")
	private Integer statusItem;

	/**
	 * 检查内容
	 */
	@Excel(name = "检查内容", width = 15)
	@ApiModelProperty(value = "检查内容")
	private String content;

	/**
	 * 检查内容
	 */
	@Excel(name = "说明/备注", width = 15)
	@ApiModelProperty(value = "说明/备注")
	private String note;

	/**
	 * 巡检项编号,同一标准下唯一
	 */
	@Excel(name = "巡检项编号,同一标准下唯一", width = 15)
	@ApiModelProperty(value = "巡检项编号,限制位数")
	private Long code;

	/**
	 * 删除状态	0.未删除 1已删除
	 */
	@Excel(name = "删除状态	0.未删除 1已删除", width = 15)
	@ApiModelProperty(value = "删除状态	0.未删除 1已删除")
	private Integer delFlag;

	/**
	 * 创建人
	 */
	@Excel(name = "创建人", width = 15)
	@ApiModelProperty(value = "创建人")
	private String createBy;

	/**
	 * 修改人
	 */
	@Excel(name = "修改人", width = 15)
	@ApiModelProperty(value = "修改人")
	private String updateBy;

	/**
	 * 创建时间
	 */
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "创建时间")
	private Date createTime;

	/**
	 * 修改时间
	 */
	@Excel(name = "修改时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "修改时间")
	private Date updateTime;


	public static final String ID = "id";

	public static final String RECORD_ID = "record_id";

	public static final String PARENT_ID = "parent_id";

	public static final String TYPE = "type";

	public static final String SEQUENCE = "sequence";

	public static final String DEL_FLAG = "del_flag";

}
