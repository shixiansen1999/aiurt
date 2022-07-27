package com.aiurt.modules.modeler.entity;

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
 * @Description: flowable流程模板定义信息
 * @Author: aiurt
 * @Date:   2022-07-27
 * @Version: V1.0
 */
@Data
@TableName("act_custom_model_info")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="act_custom_model_info对象", description="flowable流程模板定义信息")
public class ActCustomModelInfo implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
	/**模型id(act_act_re_model的id)*/
	@Excel(name = "模型id(act_act_re_model的id)", width = 15)
    @ApiModelProperty(value = "模型id(act_act_re_model的id)")
    private String modelId;
	/**流程名称*/
	@Excel(name = "流程名称", width = 15)
    @ApiModelProperty(value = "流程名称")
    private String name;
	/**流程标识*/
	@Excel(name = "流程标识", width = 15)
    @ApiModelProperty(value = "流程标识")
    private String modelKey;
	/**模型key*/
	@Excel(name = "模型key", width = 15)
    @ApiModelProperty(value = "模型key")
    private Integer modelType;
	/**modelIcon*/
	@Excel(name = "modelIcon", width = 15)
    private transient String modelIconString;

    private byte[] modelIcon;

    public byte[] getModelIcon(){
        if(modelIconString==null){
            return null;
        }
        try {
            return modelIconString.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getModelIconString(){
        if(modelIcon==null || modelIcon.length==0){
            return "";
        }
        try {
            return new String(modelIcon,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }
	/**表单类型: 0 动态表单 1路由表单*/
	@Excel(name = "表单类型: 0 动态表单 1路由表单", width = 15)
    @ApiModelProperty(value = "表单类型: 0 动态表单 1路由表单")
    private Integer formType;

	/**流程分类编码*/
	@Excel(name = "流程分类编码", width = 15)
    @ApiModelProperty(value = "流程分类编码")
    private String classifyCode;

	/**流程图Model状态*/
	@Excel(name = "流程图Model状态", width = 15)
    @ApiModelProperty(value = "流程图Model状态")
    private Integer status;
	/**默认路由表单/业务表单的URL*/
	@Excel(name = "默认路由表单/业务表单的URL", width = 15)
    @ApiModelProperty(value = "默认路由表单/业务表单的URL")
    private String businessUrl;
	/**跳过设置*/
	@Excel(name = "跳过设置", width = 15)
    @ApiModelProperty(value = "跳过设置")
    private Integer skipSet;
	/**拓展信息 状态*/
	@Excel(name = "拓展信息 状态", width = 15)
    @ApiModelProperty(value = "拓展信息 状态")
    private Integer extendStatus;
	/**排序*/
	@Excel(name = "排序", width = 15)
    @ApiModelProperty(value = "排序")
    private Integer orderNo;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
	/**创建人*/
	@Excel(name = "创建人", width = 15)
    @ApiModelProperty(value = "创建人")
    private String creator;
	/**更新时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
	/**更新人*/
	@Excel(name = "更新人", width = 15)
    @ApiModelProperty(value = "更新人")
    private String updator;
	/**删除标识*/
	@Excel(name = "删除标识", width = 15)
    @ApiModelProperty(value = "删除标识")
    private Integer delFlag;

	/**  在线表单的页面Id*/
	@Excel(name = "  在线表单的页面Id", width = 15)
    @ApiModelProperty(value = "  在线表单的页面Id")
    private String pageId;

	/**在线表单Id*/
	@Excel(name = "在线表单Id", width = 15)
    @ApiModelProperty(value = "在线表单Id")
    private String defaultFormId;

	@ApiModelProperty(value = "部署时间")
	private Date deployTime;

	//private String main
}
