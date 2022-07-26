package com.aiurt.modules.train.exam.entity;

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
import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.modules.train.question.entity.BdQuestion;
import com.aiurt.modules.train.task.entity.BdTrainTaskUser;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Description: 试卷库表
 * @Author: jeecg-boot
 * @Date:   2022-04-18
 * @Version: V1.0
 */
@Data
@TableName("bd_exam_paper")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="bd_exam_paper对象", description="试卷库表")
public class BdExamPaper implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
	/**试卷名称*/
	@Excel(name = "试卷名称", width = 15)
    @ApiModelProperty(value = "试卷名称")
    private String name;
	/**题目数量*/
	@Excel(name = "题目数量", width = 15)
    @ApiModelProperty(value = "题目数量")
    private Integer number;
	/**及格分*/
	@Excel(name = "及格分", width = 15)
    @ApiModelProperty(value = "及格分")
    private Integer passscore;
	/**问答题数量*/
	@Excel(name = "问答题数量", width = 15)
    @ApiModelProperty(value = "问答题数量")
    private Integer danumber;
	/**问答题分值*/
	@Excel(name = "问答题分值", width = 15)
    @ApiModelProperty(value = "问答题分值")
    private Integer dascore;
	/**选择题数量*/
	@Excel(name = "选择题数量", width = 15)
    @ApiModelProperty(value = "选择题数量")
    private Integer scqnumber;
	/**选择题分值*/
	@Excel(name = "选择题分值", width = 15)
    @ApiModelProperty(value = "选择题分值")
    private Integer scqscore;
	/**试卷状态：0作废1启用，默认启用*/
	@Excel(name = "试卷状态：0作废1启用，默认启用", width = 15)
    @ApiModelProperty(value = "试卷状态：0作废1启用，默认启用")
    private Integer state;
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
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**试卷总分*/
	@Excel(name = "试卷总分", width = 15)
    @ApiModelProperty(value = "试卷总分")
    private Integer score;

    @Excel(name = "题目内容集合", width = 15)
    @ApiModelProperty(value = "题目内容集合")
    @TableField(exist = false)
    private List<String> contentList;

    @Excel(name = "题目详情集合", width = 15)
    @ApiModelProperty(value = "题目详情集合")
    @TableField(exist = false)
    private List<BdQuestion> questions;

    @Excel(name = "单选题集合", width = 15)
    @ApiModelProperty(value = "单选题集合")
    @TableField(exist = false)
    private List<BdQuestion> singleChoiceList;

    @Excel(name = "多选题集合", width = 15)
    @ApiModelProperty(value = "多选题集合")
    @TableField(exist = false)
    private List<BdQuestion> multipleChoiceList;

    @Excel(name = "简答题集合", width = 15)
    @ApiModelProperty(value = "简答题集合")
    @TableField(exist = false)
    private List<BdQuestion> answerQuestionList;

    @Excel(name = "单选题数量", width = 15)
    @ApiModelProperty(value = "单选题数量")
    @TableField(exist = false)
    private Integer singleChoiceAmount;

    @Excel(name = "单选题总分数", width = 15)
    @ApiModelProperty(value = "单选题总分数")
    @TableField(exist = false)
    private Integer singleChoiceCScore;

    @Excel(name = "多选题数量", width = 15)
    @ApiModelProperty(value = "多选题数量")
    @TableField(exist = false)
    private Integer multipleChoiceAmount;

    @Excel(name = "多选题总分数", width = 15)
    @ApiModelProperty(value = "多选题总分数")
    @TableField(exist = false)
    private Integer multipleChoiceCScore;
    @Excel(name = "简答题总分数", width = 15)
    @ApiModelProperty(value = "简答题总分数")
    @TableField(exist = false)
    private Integer answerQuestionScore;

    @Excel(name = "题目id集合", width = 15)
    @ApiModelProperty(value = "题目id集合")
    @TableField(exist = false)
    private List<String> questionId;

    @ApiModelProperty(value = "考生答案")
    @TableField(exist = false)
    private List<String> examineAnswer;

    @ApiModelProperty(value = "任务id")
    @TableField(exist = false)
    private String taskId;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "考试计划时间")
    @TableField(exist = false)
    private Date examination;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "实际开始时间")
    @TableField(exist = false)
    private Date examTime;

    /**考试截止时间*/
    @ApiModelProperty(value = "考试截止时间")
    @TableField(exist = false)
    private String  examinationDeadline;

    @ApiModelProperty(value = "用户列表")
    @TableField(exist = false)
    private List<BdTrainTaskUser> userList;


    @ApiModelProperty(value = "计划任务状态")
    @TableField(exist = false)
    @Dict(dicCode = "task_state")
    private Integer taskState;

    @ApiModelProperty(value = "参考人数")
    @TableField(exist = false)
    private Integer pateNumber ;

    @ApiModelProperty(value = "缺考人数")
    @TableField(exist = false)
    private Integer lackNumber;

    @ApiModelProperty(value = "应考人数")
    @TableField(exist = false)
    private Integer takeNumber;

    /**考试类型*/
    @ApiModelProperty(value = "考试类型")
    @TableField(exist = false)
    @Dict(dicCode = "examClassify_state")
    private Integer examinationType;
    /**考试计划名称*/
    @ApiModelProperty(value = "考试计划名称")
    @TableField(exist = false)
    private String examTaskName;
    /**正确题目数*/
    @ApiModelProperty(value = "正确题目数")
    @TableField(exist = false)
    private Integer correctTopicsNumber;
    /** 错误题目数*/
    @ApiModelProperty(value = "错误题目数")
    @TableField(exist = false)
    private Integer wrongTopicsNumber;
    /**其他*/
    @ApiModelProperty(value = "其他")
    @TableField(exist = false)
    private Integer other;
    /**是否补考(1是，0：否）*/
    @ApiModelProperty(value = "是否补考(1是，0：否）")
    @TableField(exist = false)
    private Integer makeUpState;

    /**考试考试记录信息*/
    @ApiModelProperty(value = "考试考试记录信息")
    @TableField(exist = false)
    private List<BdExamRecord> bdExamRecord;

    /**
     * 正式考试记录集合
     */
    @ApiModelProperty(value = "正式考试记录集合")
    @TableField(exist = false)
    private List<BdExamRecord>formalExamRecordList;

    /**
     * 补考记录集合
     */
    @ApiModelProperty(value = "补考记录集合")
    @TableField(exist = false)
    private List<BdExamRecord> makeUpRecordList;
    /**
     * 考试记录-app传参
     */
    @ApiModelProperty(value = "考试记录-app传参")
    @TableField(exist = false)
    private String examRecordId;

    /**考试有效期*/
    @ApiModelProperty(value = "考试有效期")
    @TableField(exist = false)
    private Integer examValidityPeriod;
}
