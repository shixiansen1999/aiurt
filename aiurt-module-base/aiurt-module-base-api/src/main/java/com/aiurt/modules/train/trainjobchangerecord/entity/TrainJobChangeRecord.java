package com.aiurt.modules.train.trainjobchangerecord.entity;

import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.modules.basic.entity.DictEntity;
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

import cn.afterturn.easypoi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: train_job_change_record
 * @Author: aiurt
 * @Date:   2023-06-25
 * @Version: V1.0
 */
@Data
@TableName("train_job_change_record")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="train_job_change_record对象", description="train_job_change_record")
public class TrainJobChangeRecord extends DictEntity implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
	/**培训档案id*/
    @ApiModelProperty(value = "培训档案id")
    private String trainArchiveId;
	/**上岗岗位,0是主任，1是副主任，2是技术员，3是工班长，4是维修员*/
    @ApiModelProperty(value = "上岗岗位,0是主任，1是副主任，2是技术员，3是工班长，4是维修员")
    @Dict(dicCode = "job_status")
    private Integer jobStatus;
	/**上岗部门*/
    @ApiModelProperty(value = "上岗部门")
    @Dict(dictTable ="sys_depart",dicText = "depart_name",dicCode = "org_code")
    private String jobOrgCode;
	/**上岗时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "上岗时间")
    private Date jobTime;
	/**上岗成绩*/
    @ApiModelProperty(value = "上岗成绩")
    private Integer jobGrade;
	/**删除标志，0是未删除，1是删除*/
    @ApiModelProperty(value = "删除标志，0是未删除，1是删除")
    private Integer delFlag;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private String updateBy;
	/**更新时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
    /**部门名称*/
    @Excel(name = "上岗部门", width = 15)
    @ApiModelProperty(value = "部门名称")
    @TableField(exist = false)
    private String departName;
    /**上岗岗位名称*/
    @Excel(name = "上岗岗位", width = 15)
    @ApiModelProperty(value = "上岗岗位名称")
    @TableField(exist = false)
    private String jobName;
}
