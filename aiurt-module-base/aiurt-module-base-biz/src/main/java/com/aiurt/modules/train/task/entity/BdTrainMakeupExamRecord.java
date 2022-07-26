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
 * @Description: 培训补考记录
 * @Author: jeecg-boot
 * @Date:   2022-04-20
 * @Version: V1.0
 */
@Data
@TableName("bd_train_makeup_exam_record")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="bd_train_makeup_exam_record对象", description="培训补考记录")
public class BdTrainMakeupExamRecord implements Serializable {
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
	/**培训任务id(bd_train_task表id)*/
	@Excel(name = "培训任务id(bd_train_task表id)", width = 15)
    @ApiModelProperty(value = "培训任务id(bd_train_task表id)")
    private String trainTaskId;
    @ApiModelProperty(value = "培训任务名称(bd_train_task表id)")
    @TableField(exist = false)
    private String trainTaskName;
	/**试卷id(bd_exam_paper表id)*/
	@Excel(name = "试卷id(bd_exam_paper表id)", width = 15)
    @ApiModelProperty(value = "试卷id(bd_exam_paper表id)")
    private String examPaperId;
    @ApiModelProperty(value = "试卷名称(bd_exam_paper表id)")
    @TableField(exist = false)
    private String examPaperName;
	/**用户id*/
	@Excel(name = "用户id", width = 15)
    @ApiModelProperty(value = "用户id")
    private String userId;
    @ApiModelProperty(value = "用户姓名")
    @TableField(exist = false)
    private String userName;
	/**考试类别(1正式考试,0补考)*/
	@Excel(name = "考试类别(1正式考试,0补考)", width = 15)
    @ApiModelProperty(value = "考试类别(1正式考试,0补考)")
    private Integer examClassify;
    @ApiModelProperty(value = "考试类别字典值(1正式考试,0补考)")
    @TableField(exist = false)
    private String examClassifyText;
    @ApiModelProperty(value = "补考时间")
    @TableField(exist = false)
    private String examTime;
    @ApiModelProperty(value = "是否已经批准补考")
    private Integer isMakeup;
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "补考时间")
    private java.util.Date makeupTime;
    /**定时任务id*/
    @Excel(name = "定时任务id", width = 15)
    @ApiModelProperty(value = "定时任务id")
    private String  quartzJobId;
    /**考试记录id*/
    @ApiModelProperty(value = "考试记录id")
    private String examId;
}
