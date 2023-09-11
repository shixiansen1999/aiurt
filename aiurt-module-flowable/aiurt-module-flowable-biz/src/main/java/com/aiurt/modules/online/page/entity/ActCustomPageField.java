package com.aiurt.modules.online.page.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: act_custom_page_field
 * @Author: jeecg-boot
 * @Date:   2023-08-18
 * @Version: V1.0
 */
@Data
@TableName("act_custom_page_field")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="act_custom_page_field对象", description="act_custom_page_field")
public class ActCustomPageField implements Serializable {
    private static final long serialVersionUID = 1L;

	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
    private String id;
    /**字段标识*/
    @Excel(name = "字段标识", width = 15)
    @ApiModelProperty(value = "字段标识")
    private String field;
    /**字段名称*/
    @Excel(name = "字段名称", width = 15)
    @ApiModelProperty(value = "字段名称")
    private String name;
	/**是否只读 0-否 1-是*/
    @Excel(name = "是否只读", width = 15)
    @ApiModelProperty(value = "是否只读 0-否 1-是")
    private Boolean readOnly;
	/**页面id(关联act_custon_page的id)*/
	@Excel(name = "页面id(关联act_custon_page的id)", width = 15)
    @ApiModelProperty(value = "页面id(关联act_custon_page的id)")
    private String pageId;
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
	/**0-未删除，1已删除*/
	@Excel(name = "0-未删除，1已删除", width = 15)
    @ApiModelProperty(value = "0-未删除，1已删除")
    @TableLogic
    private Integer delFlag;
}
