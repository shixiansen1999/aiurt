package com.aiurt.modules.train.question.entity;

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
import com.aiurt.modules.train.eaxm.entity.BdExamRecordDetail;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Description: bd_question
 * @Author: jeecg-boot
 * @Date:   2022-04-18
 * @Version: V1.0
 */
@Data
@TableName("bd_question")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="bd_question对象", description="bd_question")
public class BdQuestion implements Serializable {
    private static final long serialVersionUID = 1L;

	/**题目编号*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "题目编号")
    private String id;
	/**题目内容*/
	@Excel(name = "题目内容", width = 15)
    @ApiModelProperty(value = "题目内容")
    private String content;
	/**题目类别，关联习题类别表的主键*/
	@Excel(name = "题目类别，关联习题类别表的主键", width = 15)
    @ApiModelProperty(value = "题目类别，关联习题类别表的主键")
    private String categoryId;
	/**题目类型（1选择题、2多选题、3简答题）*/
	@Excel(name = "题目类型（1选择题、2多选题、3简答题）", width = 15)
    @ApiModelProperty(value = "题目类型（1选择题、2多选题、3简答题）")
    @Dict(dicCode = "que_type")
    private Integer queType;
	/**逻辑删除：0正常，1已作废*/
	@Excel(name = "逻辑删除：0正常，1已作废", width = 15)
    @ApiModelProperty(value = "逻辑删除：0正常，1已作废")
    private Integer idel;
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
    /**类别名称*/
    @ApiModelProperty(value = "类别名称")
    @TableField(exist = false)
    private String categoryName;
    /**附件路径*/
    @ApiModelProperty(value = "附件路径")
    @TableField(exist = false)
    private String path;
    /**附件名称*/
    @ApiModelProperty(value = "附件名称")
    @TableField(exist = false)
    private String attachmentName;
    /**附件类型*/
    @ApiModelProperty(value = "附件类型")
    @TableField(exist = false)
    private String attachmentType;

    /**图片*/
    @ApiModelProperty(value = "图片")
    @TableField(exist = false)
    private String pic;

    /**视频*/
    @ApiModelProperty(value = "视频")
    @TableField(exist = false)
    private String video;

    /**资料*/
    @ApiModelProperty(value = "资料")
    @TableField(exist = false)
    private String other;

    /**图片list*/
    @ApiModelProperty(value = "图片list")
    @TableField(exist = false)
    private List<BdQuestionOptionsAtt> imageList;

    /**视频list*/
    @ApiModelProperty(value = "视频list")
    @TableField(exist = false)
    private List<BdQuestionOptionsAtt> videoList;

    /**资料list*/
    @ApiModelProperty(value = "资料list")
    @TableField(exist = false)
    private List<BdQuestionOptionsAtt> materialsList;

    /**附件list*/
    @ApiModelProperty(value = "附件list")
    @TableField(exist = false)
    private List<BdQuestionOptionsAtt> enclosureList;

    /**选项内容lis*/
    @ApiModelProperty(value = "选项内容lis")
    @TableField(exist = false)
    private String [] selectList;

    /**答案*/
    @ApiModelProperty(value = "答案")
    @TableField(exist = false)
    private String  answer;

    /**培训题库-每道题的答题*/
    @ApiModelProperty(value = "答案集合")
    @TableField(exist = false)
    private List<String>  answerList;

    /**培训题库-选项内容集合*/
    @ApiModelProperty(value = "培训题库-选项内容集合")
    @TableField(exist = false)
    private List<BdQuestionOptions> examAllQuestionOptionList;

    /**考生正式答案*/
    @ApiModelProperty(value = "考生答案")
    @TableField(exist = false)
    private List<BdExamRecordDetail>  candidatesAnswerList;

    /**考生补考答案*/
    @ApiModelProperty(value = "考生答案")
    @TableField(exist = false)
    private List<BdExamRecordDetail>  candidatesMakeUpAnswerList;

    /**app考生答案*/
    @ApiModelProperty(value = "app考生答案")
    @TableField(exist = false)
    private String appAnswer;

}
