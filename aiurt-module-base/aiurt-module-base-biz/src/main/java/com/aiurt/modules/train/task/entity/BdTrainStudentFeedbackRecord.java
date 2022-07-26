package com.aiurt.modules.train.task.entity;

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

import java.io.Serializable;

/**
 * @Description: 学员反馈记录
 * @Author: jeecg-boot
 * @Date:   2022-04-20
 * @Version: V1.0
 */
@Data
@TableName("bd_train_student_feedback_record")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="bd_train_student_feedback_record对象", description="学员反馈记录")
public class BdTrainStudentFeedbackRecord implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private java.util.Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    private java.util.Date updateTime;
	/**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private String sysOrgCode;
	/**用户id*/
	@Excel(name = "用户id", width = 15)
    @ApiModelProperty(value = "用户id")
    private String userId;
	/**培训任务id*/
	@Excel(name = "培训任务id", width = 15)
    @ApiModelProperty(value = "培训任务id")
    private String trainTaskId;
	/**问题反馈问题列表主键id（bd_train_question_feedback_ques）*/
	@Excel(name = "问题反馈问题列表主键id（bd_train_question_feedback_ques）", width = 15)
    @ApiModelProperty(value = "问题反馈问题列表主键id（bd_train_question_feedback_ques）")
    private String bdTrainQuestionFeedbackQuesId;
	/**问题反馈单选项id(bd_train_question_feedback_options)*/
	@Excel(name = "问题反馈单选项id(bd_train_question_feedback_options)", width = 15)
    @ApiModelProperty(value = "问题反馈单选项id(bd_train_question_feedback_options)")
    private String bdTrainQuestionFeedbackOptionsId;
	/**反馈时间*/
	@Excel(name = "反馈时间", width = 15, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "反馈时间")
    private java.util.Date completeTime;
	/**简答题评估结果*/
	@Excel(name = "简答题评估结果", width = 15)
    @ApiModelProperty(value = "简答题评估结果")
    private String answer;
    /**反馈主表id*/
    @Excel(name = "反馈主表id", width = 15)
    @ApiModelProperty(value = "反馈主表id")
    private String bdTrainQuestionFeedbackId;

    /**反馈问题*/
    @Excel(name = "反馈问题", width = 15)
    @ApiModelProperty(value = "反馈问题")
    @TableField(exist = false)
    private String questionName;

    /**反馈类别*/
    @Excel(name = "反馈类别", width = 15)
    @ApiModelProperty(value = "反馈类别")
    @TableField(exist = false)
    private String classifyName;
    /**选项名称*/
    @Excel(name = "选项名称", width = 15)
    @ApiModelProperty(value = "选项名称")
    @TableField(exist = false)
    private String trainQuestionFeedBackOptionsName;
}
