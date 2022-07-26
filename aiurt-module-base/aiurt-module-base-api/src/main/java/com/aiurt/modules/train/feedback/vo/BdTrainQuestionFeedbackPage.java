package com.aiurt.modules.train.feedback.vo;

import java.util.List;

import com.aiurt.modules.train.feedback.entity.BdTrainQuestionFeedbackQues;
import com.aiurt.modules.train.feedback.entity.BdTrainQuestionFeedbackOptions;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.annotation.ExcelCollection;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Description: 问题反馈主表
 * @Author: jeecg-boot
 * @Date:   2022-04-18
 * @Version: V1.0
 */
@Data
@ApiModel(value="bd_train_question_feedbackPage对象", description="问题反馈主表")
public class BdTrainQuestionFeedbackPage {

	/**主键*/
	@ApiModelProperty(value = "主键")
    private String id;
	/**创建人*/
	@ApiModelProperty(value = "创建人")
    private String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "创建日期")
    private Date createTime;
	/**更新人*/
	@ApiModelProperty(value = "更新人")
    private String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
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

	@ExcelCollection(name="问题反馈问题列表")
	@ApiModelProperty(value = "问题反馈问题列表")
	private List<BdTrainQuestionFeedbackQues> bdTrainQuestionFeedbackQuesList;
	@ExcelCollection(name="问题反馈单选项")
	@ApiModelProperty(value = "问题反馈单选项")
	private List<BdTrainQuestionFeedbackOptions> bdTrainQuestionFeedbackOptionsList;

}
