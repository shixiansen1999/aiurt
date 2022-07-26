package com.aiurt.modules.train.feedback.entity;

import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import java.util.Date;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Description: 问题反馈单选项
 * @Author: jeecg-boot
 * @Date:   2022-04-18
 * @Version: V1.0
 */
@ApiModel(value="bd_train_question_feedback对象", description="问题反馈主表")
@Data
@TableName("bd_train_question_feedback_options")
public class BdTrainQuestionFeedbackOptions implements Serializable {
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
	/**主表id（bd_train_question_feedback）*/
    @ApiModelProperty(value = "主表id（bd_train_question_feedback）")
    private String trainQuestionFeedbackId;
	/**选项名称*/
	@Excel(name = "选项名称", width = 15)
    @ApiModelProperty(value = "选项名称")
    private String name;
    /**逻辑删除，0未删除，1已删除*/
    @Excel(name = "逻辑删除，0未删除，1已删除",width = 15)
    @ApiModelProperty(value = "逻辑删除，0未删除，1已删除")
    private Integer idel;
}
