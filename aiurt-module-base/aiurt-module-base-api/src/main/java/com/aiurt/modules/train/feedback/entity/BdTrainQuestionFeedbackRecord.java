package com.aiurt.modules.train.feedback.entity;

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
import com.aiurt.modules.train.task.entity.BdTrainStudentFeedbackRecord;
import com.aiurt.modules.train.task.entity.BdTrainTeacherFeedbackRecord;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Description: bd_train_question_feedback_record
 * @Author: jeecg-boot
 * @Date:   2022-05-23
 * @Version: V1.0
 */
@Data
@TableName("bd_train_question_feedback_record")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="bd_train_question_feedback_record对象", description="bd_train_question_feedback_record")
public class BdTrainQuestionFeedbackRecord implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建日期")
    private Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "更新日期")
    private Date updateTime;
	/**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private String sysOrgCode;
	/**反馈意见表名*/
	@Excel(name = "反馈意见表名", width = 15)
    @ApiModelProperty(value = "反馈意见表名")
    private String name;
	/**反馈意见表类型*/
	@Excel(name = "反馈意见表类型", width = 15)
    @ApiModelProperty(value = "反馈意见表类型")
    private Integer classify;
	/**启用状态（1：启用，0：停用）*/
	@Excel(name = "启用状态（1：启用，0：停用）", width = 15)
    @ApiModelProperty(value = "启用状态（1：启用，0：停用）")
    private Integer state;
	/**'逻辑删除，0未删除，1已删除'*/
	@Excel(name = "'逻辑删除，0未删除，1已删除'", width = 15)
    @ApiModelProperty(value = "'逻辑删除，0未删除，1已删除'")
    private Integer idel;
	/**培训任务id*/
	@Excel(name = "培训任务id", width = 15)
    @ApiModelProperty(value = "培训任务id")
    private String trainTaskId;
    /**app分类——问题反馈问题*/
    @Excel(name = "问题反馈问题", width = 15)
    @ApiModelProperty(value = "问题反馈问题")
    @TableField(exist = false)
    List<List<BdTrainQuestionFeedbackQuesRecord>> queList;


    /**讲师问题反馈表*/
    @Excel(name = "讲师问题反馈表", width = 15)
    @ApiModelProperty(value = "讲师问题反馈表")
    @TableField(exist = false)
    private List<BdTrainTeacherFeedbackRecord> bdTrainTeacherFeedbackRecords;

    /**学员问题反馈表*/
    @Excel(name = "学员问题反馈表", width = 15)
    @ApiModelProperty(value = "学员问题反馈表")
    @TableField(exist = false)
    private List<BdTrainStudentFeedbackRecord> bdTrainStudentFeedbackRecords;

}
