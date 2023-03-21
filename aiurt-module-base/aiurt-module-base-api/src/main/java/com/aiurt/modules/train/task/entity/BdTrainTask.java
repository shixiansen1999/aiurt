package com.aiurt.modules.train.task.entity;

import com.aiurt.common.aspect.annotation.DeptFilterColumn;
import com.aiurt.common.aspect.annotation.Dict;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Description: 培训任务
 * @Author: jeecg-boot
 * @Date:   2022-04-18
 * @Version: V1.0
 */
@ApiModel(value="bd_train_task对象", description="培训任务")
@Data
@TableName("bd_train_task")
public class BdTrainTask implements Serializable ,Cloneable{
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
	/**任务计划名称*/
	@Excel(name = "任务计划名称", width = 15)
    @ApiModelProperty(value = "任务计划名称")
    private String taskName;
	/**培训部门*/
	@Excel(name = "培训部门", width = 15)
    @ApiModelProperty(value = "培训部门")
    private String taskTeamId;
    /**培训部门code*/
    @ApiModelProperty(value = "培训部门code")
    @DeptFilterColumn
    private String taskTeamCode;
	/**培训类型*/
	@Excel(name = "培训类型", width = 15)
    @ApiModelProperty(value = "培训类型")
    @Dict(dicCode = "classify_state")
    private Integer classify;
	/**培训内容id*/
	@Excel(name = "培训内容id", width = 15)
    @ApiModelProperty(value = "培训内容id")
    private String planSubId;
	/**培训内容*/
	@Excel(name = "培训内容", width = 15)
    @ApiModelProperty(value = "培训内容")
    private String planSubName;
	/**任务培训时长*/
	@Excel(name = "任务培训时长", width = 15)
    @ApiModelProperty(value = "任务培训时长")
    private java.math.BigDecimal taskHours;
	/**讲师id*/
	@Excel(name = "讲师id", width = 15)
    @ApiModelProperty(value = "讲师id")
    private String teacherId;
	/**培训地点*/
	@Excel(name = "培训地点", width = 15)
    @ApiModelProperty(value = "培训地点")
    private String address;
	/**培训对象*/
	@Excel(name = "培训对象", width = 15)
    @ApiModelProperty(value = "培训对象")
    private String trainTarget;
	/**考试计划名称*/
	@Excel(name = "考试计划名称", width = 15)
    @ApiModelProperty(value = "考试计划名称")
    private String examTaskName;
	/**考试类型*/
	@Excel(name = "考试类型", width = 15)
    @ApiModelProperty(value = "考试类型")
    @Dict(dicCode = "examClassify_state")
    private Integer examClassify;
	/**考试计划时间*/
	@Excel(name = "考试计划时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "考试计划时间")
    private Date examPlanTime;
	/**考试试卷（bd_exam_paper表的id）*/
	@Excel(name = "考试试卷（bd_exam_paper表的id）", width = 15)
    @ApiModelProperty(value = "考试试卷（bd_exam_paper表的id）")
    private String examPaperId;
	/**补考时间*/
	@Excel(name = "补考时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "补考时间")
    private Date makeUpTime;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private String remarks;
	/**是否补考(1是，0：否）*/
	@Excel(name = "是否补考(1是，0：否）", width = 15)
    @ApiModelProperty(value = "是否补考(1是，0：否）")
    private Integer makeUpState;
	/**是否作为学习资料(1是，0：否）*/
	@Excel(name = "是否作为学习资料(1是，0：否）", width = 15)
    @ApiModelProperty(value = "是否作为学习资料(1是，0：否）")
    private Integer studyResourceState;
	/**是否进行培训考试(1是，0：否）*/
	@Excel(name = "是否进行培训考试(1是，0：否）", width = 15)
    @ApiModelProperty(value = "是否进行培训考试(1是，0：否）")
    @Dict( dicCode = "examStatus_type")
    private Integer examStatus;
	/**培训表单任务状态（0：未开始，1：进行中（培训中），2：已暂停，3：结束培训，已关闭（taskState>2）*/
	@Excel(name = "培训表单任务状态（0：待发布；1：已发布；2：培训中；3：待考试；4：考试中；5：待复核；6;待评估；7：已完成;8:已删除）", width = 15)
    @ApiModelProperty(value = "培训表单任务状态（0：待发布；1：已发布；2：培训中；3：待考试；4：考试中；5：待复核；6;待评估；7：已完成）")
    @Dict(dicCode = "train_task_state")
    private Integer taskState;
	/**培训轮数*/
	@Excel(name = "培训轮数", width = 15)
    @ApiModelProperty(value = "培训轮数")
    private Integer number;
	/**实际开始培训时间*/
	@Excel(name = "实际开始培训时间", width = 15, format = "yyyy-MM-dd HH:mm")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd  HH:mm")
    @ApiModelProperty(value = "实际开始培训时间")
    private Date startTime;
	/**实际关闭培训时间*/
	@Excel(name = "实际关闭培训时间", width = 15, format = "yyyy-MM-dd HH:mm")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "实际关闭培训时间")
    private Date endTime;
    /**培训计划开始日期*/
    @Excel(name = "培训计划开始日期", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "培训计划开始日期")
    private Date startDate;
    /**培训计划结束日期*/
    @Excel(name = "培训计划结束日期", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "培训计划结束日期")
    private Date endDate;
	/**实际考试时间*/
	@Excel(name = "实际考试时间", width = 15, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "实际考试时间")
    private Date startExamTime;

    /**暂停标记(0：进行中；1：已暂停）*/
    @Excel(name = "暂停标记(0：进行中；1：已暂停）", width = 15)
    @ApiModelProperty(value = "暂停标记(0：进行中；1：已暂停）")
    private Integer stopState;

    /**试题数量*/
    @Excel(name = "试题数量", width = 15)
    @ApiModelProperty(value = "试题数量")
    @TableField(exist = false)
    private Integer examNumber;

    /**讲师名称*/
    @Excel(name = "讲师name", width = 15)
    @ApiModelProperty(value = "讲师name")
    @TableField(exist = false)
    private String teacherName;

    /**培训部门名称*/
    @Excel(name = "培训部门名称", width = 15)
    @ApiModelProperty(value = "培训部门名称")
    @TableField(exist = false)
    private String taskTeamName;

    /**培训日期范围**/
    @Excel(name = "培训日期范围",width = 15)
    @ApiModelProperty(value = "培训日期范围")
    @TableField(exist = false)
    private String trainingDateRange;
    /**培训实施任务状态*/
    @Excel(name = "培训实施任务状态（0：未开始，1：培训中，2：暂停中，3待评估 ，4已关闭）", width = 15)
    @ApiModelProperty(value = "培训实施任务状态（）")
    @TableField(exist = false)
    @Dict(dicCode = "training_state")
    private Integer trainingState;

    /**培训实施任务状态*/
    @Excel(name = "状态标记", width = 15)
    @ApiModelProperty(value = "状态标记）")
    @TableField(exist = false)
    @Dict(dicCode = "training_state")
    private Integer signState;

    /**签到状态*/
    @Excel(name = "签到状态", width = 15)
    @ApiModelProperty(value = "签到状态")
    @TableField(exist = false)
    private Integer assessmentState;

    /**已签到人数*/
    @Excel(name = "已签到人数", width = 15)
    @ApiModelProperty(value = "已签到人数")
    @TableField(exist = false)
    private Integer signedInNum;
    /**未签到人数*/
    @Excel(name = "未签到人数", width = 15)
    @ApiModelProperty(value = "未签到人数")
    @TableField(exist = false)
    private Integer notSignedInNum;

    /**应参训人数*/
    @Excel(name = "应参训人数", width = 15)
    @ApiModelProperty(value = "应参训人数")
    @TableField(exist = false)
    private Integer numberTrainees;

    /**及格人数*/
    @Excel(name = "及格人数", width = 15)
    @ApiModelProperty(value = "及格人数")
    @TableField(exist = false)
    private Integer passNumber;

    /**未及格人数*/
    @Excel(name = "未及格人数", width = 15)
    @ApiModelProperty(value = "未及格人数")
    @TableField(exist = false)
    private Integer notPassNumber;

    /**缺考人数*/
    @Excel(name = "缺考人数", width = 15)
    @ApiModelProperty(value = "缺考人数")
    @TableField(exist = false)
    private Integer absentPassNumber;

    /**开始时间*/
    @Excel(name = "开始时间", width = 15)
    @ApiModelProperty(value = "开始时间")
    @TableField(exist = false)
    private String trainStartDate;
    /**结束时间*/
    @Excel(name = "结束时间", width = 15)
    @ApiModelProperty(value = "结束时间")
    @TableField(exist = false)
    private String trainEndDate;

    /**考试时间*/
    @Excel(name = "考试日期", width = 15)
    @ApiModelProperty(value = "考试rq")
    @TableField(exist = false)
    private String examPlanDate;

    /**二维码*/
    @Excel(name = "二维码", width = 15)
    @ApiModelProperty(value = "二维码")
    @TableField(exist = false)
    private byte[] pngData;

    /**考试有效期*/
    @Excel(name = "考试有效期", width = 15)
    @ApiModelProperty(value = "考试有效期")
    private Integer examValidityPeriod;


    /**考试截止时间*/
    @ApiModelProperty(value = "考试截止时间")
    @TableField(exist = false)
    private String  examinationDeadline;

    /**定时任务id*/
    @Excel(name = "定时任务id", width = 15)
    @ApiModelProperty(value = "定时任务id")
    private String  quartzJobId;

    /**试卷名称*/
    @Excel(name = "试卷名称", width = 15)
    @ApiModelProperty(value = "试卷名称")
    @TableField(exist = false)
    private String examName;

    /**培训对象ids*/
    @Excel(name = "培训对象ids", width = 15)
    @ApiModelProperty(value = "培训对象ids")
    @TableField(exist = false)
    private List<String> userIds;

    @ApiModelProperty(value = "pageNo")
    @TableField(exist = false)
    private Integer pageNo;

    @ApiModelProperty(value = "pageSize")
    @TableField(exist = false)
    private Integer pageSize;
    /**
     * 克隆方法
     * @return
     * @throws CloneNotSupportedException
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
