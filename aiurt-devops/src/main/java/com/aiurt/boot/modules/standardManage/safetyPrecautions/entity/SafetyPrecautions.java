package com.aiurt.boot.modules.standardManage.safetyPrecautions.entity;

import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 安全事项
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
@Data
@TableName("safety_precautions")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="safety_precautions对象", description="安全事项")
public class SafetyPrecautions {

	/**主键ID*/
	@TableId(type= IdType.AUTO)
    @ApiModelProperty(value = "主键ID")
	public Long id;
	/**标题*/
	@Excel(name = "安全事项名称", width = 15)
    @ApiModelProperty(value = "标题")
	public String title;
	/**事项类型（数据字典获取）*/
	@Excel(name = "安全事项类型", width = 15,dicCode = "types_of_safety_matters" ,dicText = "item_text")
    @ApiModelProperty(value = "事项类型（数据字典获取）")
	public Integer type;
	/**状态 0-未生效 1-已生效*/
	@Excel(name = "状态", width = 15,replace = {"无效_0","有效_1"})
    @ApiModelProperty(value = "状态 0-未生效 1-已生效")
	public Integer status;
	/**内容*/
	@Excel(name = "安全事项内容", width = 15)
    @ApiModelProperty(value = "内容")
	public String content;
	/**删除状态*/
//	@Excel(name = "删除状态", width = 15)
    @ApiModelProperty(value = "删除状态")
	public Integer delFlag;
	/**创建者*/
	@Excel(name = "创建人", width = 15)
    @ApiModelProperty(value = "创建者")
	public String createBy;
	/**更新者*/
//	@Excel(name = "更新者", width = 15)
    @ApiModelProperty(value = "更新者")
	public String updateBy;
	/**创建时间*/
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	public Date createTime;
	/**更新时间*/
//	@Excel(name = "更新时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
	public Date updateTime;
}
