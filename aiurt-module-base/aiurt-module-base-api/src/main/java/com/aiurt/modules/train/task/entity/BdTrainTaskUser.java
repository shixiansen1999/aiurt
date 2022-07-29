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
import com.aiurt.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 培训任务人员
 * @Author: jeecg-boot
 * @Date:   2022-04-20
 * @Version: V1.0
 */
@Data
@TableName("bd_train_task_user")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="bd_train_task_user对象", description="培训任务人员")
public class BdTrainTaskUser implements Serializable {
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
    @Excel(name = "所属部门", width = 15)
    @ApiModelProperty(value = "所属部门")
    private String sysOrgCode;
	/**用户id*/
    @Excel(name = "用户id", width = 15)
    @ApiModelProperty(value = "用户id")
    private String userId;
	/**用户名*/
	@Excel(name = "用户名", width = 15)
    @ApiModelProperty(value = "用户名")
    private String userName;
	/**班组id*/
    @ApiModelProperty(value = "班组id")
    private String teamId;
	/**主表id（bd_train_task表id）*/
    @Excel(name = "任务id", width = 15)
    @ApiModelProperty(value = "任务id）")
    private String trainTaskId;
	/**签到状态(1已签到.0未签到)*/
    @ApiModelProperty(value = "签到状态(1已签到.0未签到)")
    @Dict(dicCode = "signState_type")
    private Integer signState;
    /**已签到人数*/
    @ApiModelProperty(value = "已签到人数")
    @TableField(exist = false)
    private Integer signedInNum;
    /**未签到人数*/
    @ApiModelProperty(value = "未签到人数")
    @TableField(exist = false)
    private Integer notSignedInNum;
	/**反馈转态(1已反馈,0未反馈)*/
    @ApiModelProperty(value = "反馈转态(1已反馈,0未反馈)")
    @Dict(dicCode = "feedState_type")
    private Integer feedState;
    @ApiModelProperty(value = "培训任务")
    @TableField(exist = false)
    private BdTrainTask bdTrainTask;
    /**签到时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "签到时间")
    @TableField(exist = false)
    private Date signTime;
    /**反馈时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "反馈时间")
    @TableField(exist = false)
    private Date completeTime;

    /**考试记录id*/
    @ApiModelProperty(value = "考试记录id")
    @TableField(exist = false)
    private String recordId;

    /**得分*/
    @Excel(name = "得分", width = 15)
    @ApiModelProperty(value = "得分(考试成绩)")
    @TableField(exist = false)
    private Integer score;

    /**培训表单任务状态（0：未开始，1：进行中（培训中），2：已暂停，3：结束培训，已关闭（taskState>2）*/
    @ApiModelProperty(value = "培训表单任务状态（0：待发布；1：已发布；2：培训中；3：待考试；4：考试中；5：待复核；6;待评估；7：已完成）")
    @TableField(exist = false)
    @Dict(dicCode = "train_task_state")
    private Integer taskState;

    /**培训实施任务状态*/
    @ApiModelProperty(value = "培训实施任务状态（）")
    @TableField(exist = false)
    @Dict(dicCode = "training_state")
    private Integer trainingState;

    /**培训类型*/
    @ApiModelProperty(value = "培训类型")
    @Dict(dicCode = "classify_state")
    @TableField(exist = false)
    private Integer classify;

    /**有无考试*/
    @ApiModelProperty(value = "是否进行培训考试(1是，0：否）")
    @Dict( dicCode = "examStatus_type")
    @TableField(exist = false)
    private Integer examStatus;

    /**考试类型*/
    @ApiModelProperty(value = "考试类型")
    @TableField(exist = false)
    @Dict(dicCode = "examClassify_state")
    private Integer examClassify;

    /**是否补考(1是，0：否）*/
    @ApiModelProperty(value = "是否补考(1是，0：否）")
    @TableField(exist = false)
    @Dict(dicCode = "common_states")
    private Integer makeUpState;

    /**是否补考*/
    @ApiModelProperty(value = "是否补考")
    @TableField(exist = false)
    private String makeUpStateName;

    /**考试时间（yyyy-MM-dd）如果是补考就是补考时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd  HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd  HH:mm:ss")
    @ApiModelProperty(value = "考试时间（yyyy-MM-dd）如果是补考就是补考时间")
    @TableField(exist = false)
    private Date examTime;

    /**交卷时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd  HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd  HH:mm:ss")
    @ApiModelProperty(value = "交卷时间")
    @TableField(exist = false)
    private Date submitTime;

    /**考试用时*/
    @ApiModelProperty(value = "考试用时")
    @TableField(exist = false)
    private Integer useTime;


    /**是否及格（1 通过 0 未通过）*/
    @ApiModelProperty(value = "是否及格（1 通过 0 未通过）")
    @TableField(exist = false)
    private Integer isPass;


    /**考试状态*/
    @ApiModelProperty(value = "考试状态")
    @TableField(exist = false)
    private String examinationStatus;

    /**考试状态*/
    @ApiModelProperty(value = "是否及格")
    @TableField(exist = false)
    private String passName;

    /**考试试卷（bd_exam_paper表的id）*/
    @Excel(name = "试卷id", width = 15)
    @ApiModelProperty(value = "试卷id")
    @TableField(exist = false)
    private String examPaperId;

    /**补考次数*/
    @ApiModelProperty(value = "补考次数")
    @TableField(exist = false)
    private Integer examFrequency;


    /**补考次数*/
    @ApiModelProperty(value = "是否发布考试结果")
    @TableField(exist = false)
    private Integer isRelease;

    /**补考次数*/
    @ApiModelProperty(value = "补考标记")
    @TableField(exist = false)
    private Integer makeup;

    /**考试状态*/
    @ApiModelProperty(value = "考试状态（0：未考试，1：考试中，2：已考试）")
    @TableField(exist = false)
    @Dict(dicCode = "exam_state")
    private String examState;
}
