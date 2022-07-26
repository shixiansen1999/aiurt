package com.aiurt.modules.train.question.entity;

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
 * @Description: bd_question_options
 * @Author: jeecg-boot
 * @Date:   2022-04-18
 * @Version: V1.0
 */
@Data
@TableName("bd_question_options")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="bd_question_options对象", description="bd_question_options")
public class BdQuestionOptions implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
	/**习题id,关联习题表的主键*/
	@Excel(name = "习题id,关联习题表的主键", width = 15)
    @ApiModelProperty(value = "习题id,关联习题表的主键")
    private String questionId;
	/**选项内容*/
	@Excel(name = "选项内容", width = 15)
    @ApiModelProperty(value = "选项内容")
    private String content;
	/**是否正确 正确1 错误0*/
	@Excel(name = "是否正确 正确1 错误0", width = 15)
    @ApiModelProperty(value = "是否正确 正确1 错误0")
    private Integer isRight;
	/**选项排序号*/
	@Excel(name = "选项排序号", width = 15)
    @ApiModelProperty(value = "选项排序号")
    private Integer isort;
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
}
