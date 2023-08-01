package com.aiurt.modules.online.page.entity;

import java.io.Serializable;
import java.util.Date;

import com.aiurt.common.aspect.annotation.Dict;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 设计表单
 * @Author: aiurt
 * @Date:   2022-10-26
 * @Version: V1.0
 */
@Data
@TableName(value = "act_custom_page", autoResultMap = true)
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="act_custom_page对象", description="设计表单")
public class ActCustomPage implements Serializable {

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
    @Dict(dicCode = "org_code", dicText = "depart_name", dictTable = "sys_depart")
    private String sysOrgCode;
	/**表单名称*/
	@Excel(name = "表单名称", width = 15)
    @ApiModelProperty(value = "表单名称")
    private String pageName;
	/**表单全局属性*/
	@Excel(name = "表单全局属性", width = 15)
    @ApiModelProperty(value = "表单全局属性")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private JSONObject pageJson;
	/**表单属性*/
	@Excel(name = "表单属性", width = 15)
    @ApiModelProperty(value = "表单属性")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private JSONArray pageContentJson;
	/**版本号*/
	@Excel(name = "版本号", width = 15)
    @ApiModelProperty(value = "版本号")
    private Integer pageVersion;
    /**后台接口路径*/
	@Excel(name = "后台接口路径", width = 15)
    @ApiModelProperty(value = "后台接口路径")
    private String pageInterfacePath;
    /**前端页面路径*/
	@Excel(name = "前端页面路径", width = 15)
    @ApiModelProperty(value = "前端页面路径")
    private String pagePath;
    /**表单类型：0-静态表单，1-动态表单*/
	@Excel(name = "表单类型：0-静态表单，1-动态表单", width = 15)
    @ApiModelProperty(value = "表单类型：0-静态表单，1-动态表单")
    private Integer pageType;

    /**
     * 删除状态 0-未删除 1-已删除
     */
    @ApiModelProperty(value = "删除状态 0-未删除 1-已删除")
    @TableLogic
    private Integer delFlag;

}
