package com.aiurt.modules.train.traindegreerecord.dto;

import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.modules.basic.entity.DictEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Description: train_degree_record
 * @Author: aiurt
 * @Date:   2023-06-25
 * @Version: V1.0
 */
@Data
public class DegreeRecordExcelDTO extends DictEntity {
    /**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
	/**培训档案id*/
	@Excel(name = "培训档案id", width = 15)
    @ApiModelProperty(value = "培训档案id")
    private String trainArchiveId;
	/**学历,0小学、1初中、2高中、3大专、4大学、5研究生*/
	@Excel(name = "学历,0小学、1初中、2高中、3大专、4大学、5研究生", width = 15)
    @ApiModelProperty(value = "学历,0小学、1初中、2高中、3大专、4大学、5研究生")
    @Dict(dicCode = "degree")
    private Integer degree;
	/**毕业形式*/
	@Excel(name = "毕业形式", width = 15)
    @ApiModelProperty(value = "毕业形式")
    @Dict(dicCode = "graduation_form")
    private Integer graduationForm;
	/**毕业时间*/
	@Excel(name = "毕业时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "毕业时间")
    private Date graduationTime;
	/**毕业院校*/
	@Excel(name = "毕业院校", width = 15)
    @ApiModelProperty(value = "毕业院校")
    private String graduationSchool;
	/**所学专业*/
	@Excel(name = "所学专业", width = 15)
    @ApiModelProperty(value = "所学专业")
    private String majorsStudied;
	/**学历类型，0是第一学历，1是最高学历*/
	@Excel(name = "学历类型，0是第一学历，1是最高学历", width = 15)
    @ApiModelProperty(value = "学历类型，0是第一学历，1是最高学历")
    private Integer degreeType;
	/**删除标志，0是未删除，1是删除*/
	@Excel(name = "删除标志，0是未删除，1是删除", width = 15)
    @ApiModelProperty(value = "删除标志，0是未删除，1是删除")
    private Integer delFlag;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private String updateBy;
	/**更新时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
    /**学位名称*/
    @ApiModelProperty(value = "学位名称")
    @TableField(exist = false)
    private String degreeName;
    /**统招方式名称*/
    @ApiModelProperty(value = "统招方式名称")
    @TableField(exist = false)
    private String graduationFormName;
    @TableField(exist = false)
    @ApiModelProperty(value = "错误原因")
    private String changeMistake;
    @TableField(exist = false)
    @ApiModelProperty(value = "毕业时间")
    private String schoolEndTime;
}
