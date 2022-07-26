package com.aiurt.modules.train.eaxm.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 答题详情
 * @Author: jeecg-boot
 * @Date:   2022-04-18
 * @Version: V1.0
 */
@Data
@TableName("bd_exam_record_detail")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="bd_exam_record_detail对象", description="答题详情")
public class BdExamRecordDetail implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
	/**考试记录id(关联bd_exam_record中的id)*/
	@Excel(name = "考试记录id(关联bd_exam_record中的id)", width = 15)
    @ApiModelProperty(value = "考试记录id(关联bd_exam_record中的id)")
    private String examRecordId;
	/**问题id(关联bd_question中的id)*/
	@Excel(name = "问题id(关联bd_question中的id)", width = 15)
    @ApiModelProperty(value = "问题id(关联bd_question中的id)")
    private String queId;
	/**1正确2错误3不完全正确*/
	@Excel(name = "1正确2错误3不完全正确", width = 15)
    @ApiModelProperty(value = "1正确2错误3不完全正确")
    private Integer isTrue;
	/**本题得分*/
	@Excel(name = "本题得分", width = 15)
    @ApiModelProperty(value = "本题得分")
    private Integer score;
	/**考生答案*/
	@Excel(name = "考生答案", width = 15)
    @ApiModelProperty(value = "考生答案")
    private String answer;
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
	/**排序序号*/
	@Excel(name = "排序序号", width = 15)
    @ApiModelProperty(value = "排序序号")
    private Integer indexId;
}
