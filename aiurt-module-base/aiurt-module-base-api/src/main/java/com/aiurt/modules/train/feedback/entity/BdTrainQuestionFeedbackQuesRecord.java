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
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Description: bd_train_question_feedback_ques_record
 * @Author: jeecg-boot
 * @Date:   2022-05-23
 * @Version: V1.0
 */
@Data
@TableName("bd_train_question_feedback_ques_record")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="bd_train_question_feedback_ques_record对象", description="bd_train_question_feedback_ques_record")
public class BdTrainQuestionFeedbackQuesRecord implements Serializable {
    private static final long serialVersionUID = 1L;

	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
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
	/**主表id（bd_train_question_feedback）*/
	@Excel(name = "主表id（bd_train_question_feedback）", width = 15)
    @ApiModelProperty(value = "主表id（bd_train_question_feedback）")
    private String trainQuestionFeedbackId;
	/**反馈类别*/
	@Excel(name = "反馈类别", width = 15)
    @ApiModelProperty(value = "反馈类别")
    private String classifyName;
	/**反馈问题*/
	@Excel(name = "反馈问题", width = 15)
    @ApiModelProperty(value = "反馈问题")
    private String questionName;
	/**问题类型(0:单选,1简答)*/
	@Excel(name = "问题类型(0:单选,1简答)", width = 15)
    @ApiModelProperty(value = "问题类型(0:单选,1简答)")
    private Integer questionClassify;
	/**逻辑删除，0未删除，1已删除*/
	@Excel(name = "逻辑删除，0未删除，1已删除", width = 15)
    @ApiModelProperty(value = "逻辑删除，0未删除，1已删除")
    private Integer idel;
    /**问题反馈选项*/
    @Excel(name = "问题反馈选项", width = 15)
    @ApiModelProperty(value = "问题反馈单选项")
    @TableField(exist = false)
    private List<BdTrainQuestionFeedbackOptionsRecord> options;
    /**app分类——问题反馈问题*/
    @Excel(name = "问题反馈问题", width = 15)
    @ApiModelProperty(value = "问题反馈问题")
    @TableField(exist = false)
    List<List<BdTrainQuestionFeedbackQuesRecord>> queList;

}
