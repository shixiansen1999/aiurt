package com.aiurt.modules.schedule.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
import java.util.List;

/**
 * @Description: schedule_rule
 * @Author: swsc
 * @Date:   2021-09-23
 * @Version: V1.0
 */
@Data
@TableName("schedule_rule")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="排班规则对象", description="排班规则对象")
public class ScheduleRule {

	/**id*/
	@TableId(type= IdType.AUTO)
    @ApiModelProperty(value = "id")
	private  Integer  id;

	/**名称*/
	@Excel(name = "名称", width = 15)
    @ApiModelProperty(value = "名称")
	private  String  name;

    /**天数*/
    @Excel(name = "周期（天数）", width = 15)
    @ApiModelProperty(value = "周期（天数）")
    private  Integer  days;

	/**删除标志*/
	@Excel(name = "删除标志", width = 15)
    @ApiModelProperty(value = "删除标志")
	private  Integer  delFlag;

	/**创建时间*/
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**创建人*/
    @Excel(name = "创建人", width = 15)
    @ApiModelProperty(value = "创建人")
    private  String  createBy;

    /**修改人*/
    @Excel(name = "修改人", width = 15)
    @ApiModelProperty(value = "修改人")
    private  String  updateBy;

    /**
     * 更新时间
     */
    @Excel(name = "更新时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @TableField(exist = false)
    private String itemNames;

	@TableField(exist = false)
	private Integer[] names;
	@TableField(exist = false)
	private Integer[] keys;
	@TableField(exist = false)
	private String way;//排班方式

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String DEL_FLAG = "del_flag";
    private static final String CREATE_TIME = "create_time";
    private static final String UPDATE_TIME = "update_time";

    @TableField(exist = false)
    private List<Integer> itemIds;
}
