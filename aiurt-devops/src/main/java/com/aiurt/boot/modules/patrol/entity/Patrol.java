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

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @Description: 巡检标准
 * @Author: swsc
 * @Date:   2021-09-14
 * @Version: V1.0
 */
@Data
@TableName("patrol")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="patrol对象", description="巡检标准")
public class Patrol {

	/**主键id		自动递增*/
	@TableId(type= IdType.AUTO)
    @ApiModelProperty(value = "主键id")
	private Long id;

	/**标题 巡检表名称*/
	@Excel(name = "标题", width = 15)
    @ApiModelProperty(value = "标题")
	@NotNull(message = "名称不能为空")
	private String title;

	/**适用系统*/
    @ApiModelProperty(value = "适用系统")
	private String types;

	/**状态 	0-未生效 1-已生效*/
	@Excel(name = "状态", width = 15)
    @ApiModelProperty(value = "状态 	0-未生效 1-已生效")
	private Integer status;

	/**巡检频率		1.一天1次 2.一周2次 3.一周1次*/
	@Excel(name = "巡检频率", width = 15)
    @ApiModelProperty(value = "巡检频率		1.一天1次 2.一周2次 3.一周1次")
	private Integer tactics;

	/**年度第几周*/
	@Excel(name = "年度第几周", width = 15)
    @ApiModelProperty(value = "巡检频率为2与3时有值,多条英文逗号分隔")
	private String  dayOfWeek;

	/**适用组织id集合		英文逗号分割*/
	@Excel(name = "组织id集合", width = 15)
    @ApiModelProperty(value = "适用组织id集合		英文逗号分割")
	private String organizationIds;

	@Excel(name = "说明", width = 15)
	@ApiModelProperty(value = "说明")
	private String note;

	/**删除状态		0.未删除 1已删除*/
	@Excel(name = "删除状态", width = 15)
    @ApiModelProperty(value = "删除状态		0.未删除 1已删除")
	private Integer delFlag;

	/**创建时间		CURRENT_TIMESTAMP*/
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	private Date createTime;

	/**修改时间		根据当前时间戳更新*/
	@Excel(name = "修改时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
	private Date updateTime;

	/**创建人*/
	@Excel(name = "创建人", width = 15)
    @ApiModelProperty(value = "创建人")
	private String createBy;

	/**修改人*/
	@Excel(name = "修改人", width = 15)
    @ApiModelProperty(value = "修改人")
	private String updateBy;



	public static final String ID = "id";
	public static final String TITLE = "title";
	public static final String TYPES = "types";
	public static final String STATUS = "status";
	public static final String TACTICS = "tactics";
	public static final String DAY_OF_WEEK = "day_of_week";
	public static final String ORGANIZATION_IDS = "organization_ids";
	public static final String DEL_FLAG = "del_flag";
	public static final String CREATE_TIME = "create_time";
	public static final String UPDATE_TIME = "update_time";
	public static final String CREATE_BY = "create_by";
	public static final String UPDATE_BY = "update_by";
	public static final String NOTE = "note";
}
