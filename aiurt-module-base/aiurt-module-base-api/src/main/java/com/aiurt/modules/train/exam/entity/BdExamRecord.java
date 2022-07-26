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
import com.aiurt.modules.train.exam.dto.BdAchievementDTO;
import com.aiurt.modules.train.exam.dto.BdExamIdDTO;
import com.aiurt.modules.train.exam.dto.BdExamRecordDTO;
import com.aiurt.modules.train.task.entity.BdTrainMakeupExamRecord;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Description: 考试记录
 * @Author: jeecg-boot
 * @Date:   2022-04-18
 * @Version: V1.0
 */
@Data
@TableName("bd_exam_record")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="bd_exam_record对象", description="考试记录")
public class BdExamRecord implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
	/**考试人员id,关联人员表中的id*/
	@Excel(name = "考试人员id,关联人员表中的id", width = 15)
    @ApiModelProperty(value = "考试人员id,关联人员表中的id")
    private String userId;
	/**培训任务id，关联bd_exam_record表id*/
	@Excel(name = "培训任务id，关联bd_exam_record表id", width = 15)
    @ApiModelProperty(value = "培训任务id，关联bd_exam_record表id")
    private String trainTaskId;
	/**考试时间（yyyy-MM-dd）如果是补考就是补考时间*/
	@Excel(name = "考试时间（yyyy-MM-dd）如果是补考就是补考时间", width = 15, format = "yyyy-MM-dd HH:mm")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "考试时间（yyyy-MM-dd）如果是补考就是补考时间")
    private Date examTime;
	/**是否交卷  0未交卷  1已交卷*/
	@Excel(name = "是否交卷  0未交卷  1已交卷", width = 15)
    @ApiModelProperty(value = "是否交卷  0未交卷  1已交卷")
    private Integer isSubmit;
	/**答卷时间*/
	@Excel(name = "答卷时间", width = 15, format = "yyyy-MM-dd HH:mm")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "答卷时间")
    private Date answerTime;
	/**是否需要批卷 0是  1否*/
	@Excel(name = "是否需要批卷 0是  1否", width = 15)
    @ApiModelProperty(value = "是否需要批卷 0是  1否")
    private Integer correct;
	/**交卷时间*/
	@Excel(name = "交卷时间", width = 15, format = "yyyy-MM-dd  HH:mm")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd  HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd  HH:mm")
    @ApiModelProperty(value = "交卷时间")
    private Date submitTime;
	/**考试用时*/
	@Excel(name = "考试用时", width = 15)
    @ApiModelProperty(value = "考试用时")
    private Integer usetime;
	/**总得分*/
	@Excel(name = "总得分", width = 15)
    @ApiModelProperty(value = "总得分")
    private Integer score;
	/**是否已经发布考试结果（0待复核、1已发布）*/
	@Excel(name = "0未开始、1进行中，2待复核，3已结束", width = 15)
    @ApiModelProperty(value = "0未开始、1进行中，2待复核，3已结束")
    @Dict(dicCode = "task_tate")
    private String isRelease;
	/**是否及格（1 通过 0 未通过）*/
	@Excel(name = "是否及格（1 通过 0 未通过）", width = 15)
    @ApiModelProperty(value = "是否及格（1 通过 0 未通过）")
    @Dict(dicCode = "is_pass")
    private Integer isPass;
	/**考试试卷（bd_exam_paper表的id）*/
	@Excel(name = "考试试卷（bd_exam_paper表的id）", width = 15)
    @ApiModelProperty(value = "考试试卷（bd_exam_paper表的id）")
    private String examPaperId;
	/**考试类别(1正式考试，0补考)*/
	@Excel(name = "考试类别(1正式考试，0补考)", width = 15)
    @ApiModelProperty(value = "考试类别(1正式考试，0补考)")
    @Dict(dicCode = "exam_classify")
    private Integer examClassify;
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

    /**考试计划名称*/
    @ApiModelProperty(value = "考试计划名称")
    @TableField(exist = false)
	private String examTaskName;

    /**计划任务状态*/
    @ApiModelProperty(value = "计划任务状态")
    @TableField(exist = false)
    private Integer taskState;

    /**试卷名称*/
    @ApiModelProperty(value = "试卷名称")
    @TableField(exist = false)
    private String paperName;
    /**用户名字*/
    @ApiModelProperty(value = "用户名字")
    @TableField(exist = false)
    private String userName;

    /**题目数量*/
    @ApiModelProperty(value = "题目数量")
    @TableField(exist = false)
    private Integer number;

    /**及格分*/
    @ApiModelProperty(value = "及格分")
    @TableField(exist = false)
    private Integer passcode;

    /**总分*/
    @ApiModelProperty(value = "总分")
    @TableField(exist = false)
    private Integer paperScore;

    /**选项内容*/
    @ApiModelProperty(value = "选项内容")
    @TableField(exist = false)
    private List<BdExamRecordDTO> bdExamRecordDTOList;

    /**答题详情list*/
    @ApiModelProperty(value = "答题详情list")
    @TableField(exist = false)
    private List<BdExamRecordDetail> bdExamRecordDetailList;

    /**是否进行培训考试*/
    @ApiModelProperty(value = "是否进行培训考试")
    @TableField(exist = false)
    private Integer examStatus;

    /**考试类型*/
    @ApiModelProperty(value = "考试类型")
    @TableField(exist = false)
    @Dict(dicCode = "examClassify_state")
    private Integer examinationType;

    /**时间*/
    @ApiModelProperty(value = "时间")
    @TableField(exist = false)
    private String time;


    /**考试日期*/
    @Excel(name = "考试日期", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "考试日期")
    @TableField(exist = false)
    private Date examination;

    /**考试日期*/
    @Excel(name = "实际考试时间", width = 15, format = "yyyy-MM-dd  HH:mm")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd  HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd  HH:mm")
    @ApiModelProperty(value = "实际考试时间")
    @TableField(exist = false)
    private Date examPlanTime;


    /**考试日期*/
    @Excel(name = "考试日期", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "考试日期")
    @TableField(exist = false)
    private String  times;



    /**考试截止时间*/
    @ApiModelProperty(value = "考试截止时间")
    @TableField(exist = false)
    private String  examinationDeadline;

    /**
     * 考试记录集合
     */
    @ApiModelProperty(value = "考试记录集合")
    @TableField(exist = false)
    private List<BdAchievementDTO> bdAchievementDTOList;

    /**
     * 补考记录集合
     */
    @ApiModelProperty(value = "补考记录集合")
    @TableField(exist = false)
    private List<BdTrainMakeupExamRecord> bdTrainMakeupExamRecordList;
    /**考生补考答案*/
    @ApiModelProperty(value = "考生答案")
    @TableField(exist = false)
    private List<BdExamRecordDetail>  makeUpAnswerList;
    /**考生正式考试答案*/
    @ApiModelProperty(value = "考生答案")
    @TableField(exist = false)
    private List<BdExamRecordDetail>  formalExamAnswerList;

    /**导入*/
    @ApiModelProperty(value = "导入列表id集合")
    @TableField(exist = false)
    private List<BdExamIdDTO>  bdExamIdDTOList;


    /**考试时间（yyyy-MM-dd）如果是补考就是补考时间*/
    @Excel(name = "考试时间（yyyy-MM-dd）如果是补考就是补考时间", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "考试时间（yyyy-MM-dd）如果是补考就是补考时间")
    @TableField(exist = false)
    private Date testTime;

    /**考试任务状态*/
    @Excel(name = "考试任务状态（0：未开始，1：进行中，2：待复核，3：已结束）", width = 15)
    @ApiModelProperty(value = "考试任务状态（）")
    @TableField(exist = false)
    @Dict(dicCode = "task_tate")
    private Integer examTaskState;

    /**考试任务状态*/
    @ApiModelProperty(value = "考试任务状态")
    @TableField(exist = false)
    private List<Integer> taskStates;

    /**考试状态*/
    @Excel(name = "考试状态（0：未考试，1：考试中，2：已考试）", width = 15)
    @ApiModelProperty(value = "考试状态（）")
    @Dict(dicCode = "exam_state")
    private String examState;

    /**考试状态*/
    @ApiModelProperty(value = "考试计划状态")
    @TableField(exist = false)
    private List<String> isReleases;

    /**考试状态*/
    @ApiModelProperty(value = "补考考试计划状态")
    @TableField(exist = false)
    private List<String> makeUpExamIsReleases;

    /**补考次数标识*/
    @ApiModelProperty(value = "补考次数标识")
    @TableField(exist = false)
    private Integer  identification;

    /**考试有效期*/
    @ApiModelProperty(value = "考试有效期")
    @TableField(exist = false)
    private Integer examValidityPeriod;
}
