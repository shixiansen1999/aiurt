package com.aiurt.boot.drools.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import com.aiurt.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: drools_rule
 * @Author: aiurt
 * @Date:   2023-03-09
 * @Version: V1.0
 */
@Data
@TableName("drools_rule")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="drools_rule对象", description="drools_rule")
public class DroolsRule implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键ID*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键ID")
    private String id;
	/**规则名称*/
	@Excel(name = "规则名称", width = 15)
    @ApiModelProperty(value = "规则名称")
    private String name;
	/**对规则的描述*/
	@Excel(name = "对规则的描述", width = 15)
    @ApiModelProperty(value = "对规则的描述")
    private String description;
	/**规则的文本内容*/
	@Excel(name = "规则的文本内容", width = 15)
    @ApiModelProperty(value = "规则的文本内容")
    private String rule;
	/**删除状态：0未删除 1已删除  默认0*/
	@Excel(name = "删除状态：0未删除 1已删除  默认0", width = 15)
    @ApiModelProperty(value = "删除状态：0未删除 1已删除  默认0")
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
}
