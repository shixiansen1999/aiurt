package com.aiurt.modules.online.workflowapi.entity;

import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.modules.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
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
import java.util.Date;

/**
 * @Description: act_custom_interface
 * @Author: wgp
 * @Date:   2023-07-25
 * @Version: V1.0
 */
@Data
@TableName("act_custom_interface")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="act_custom_interface对象", description="act_custom_interface")
public class ActCustomInterface extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
	/**接口名称*/
	@Excel(name = "接口名称", width = 15)
    @ApiModelProperty(value = "接口名称")
    private String name;
	/**接口路径*/
	@Excel(name = "接口路径", width = 15)
    @ApiModelProperty(value = "接口路径")
    private String path;
	/**接口分类*/
	@Excel(name = "接口分类", width = 15)
    @ApiModelProperty(value = "接口分类")
    private Integer type;
    /**接口所属模块*/
    @Excel(name = "接口所属模块", width = 15)
    @ApiModelProperty(value = "接口所属模块")
    private Integer module;
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
    /**
     * 删除状态 0-未删除 1-已删除
     */
    @ApiModelProperty(value = "删除状态 0-未删除 1-已删除")
    @TableLogic
    private Integer delFlag;
}
