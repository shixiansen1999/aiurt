package com.aiurt.modules.basic.entity;

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
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: sys_attachment
 * @Author: gaowei
 * @Date:   2022-06-22
 * @Version: V1.0
 */
@Data
@TableName("sys_attachment")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="sys_attachment对象", description="sys_attachment")
public class SysAttachment implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private String id;
	/**文件路径*/
	@Excel(name = "文件路径", width = 15)
    @ApiModelProperty(value = "文件路径")
    private String filePath;
	/**文件名称*/
	@Excel(name = "文件名称", width = 15)
    @ApiModelProperty(value = "文件名称")
    private String fileName;
	/**文件类型*/
	@Excel(name = "文件类型", width = 15)
    @ApiModelProperty(value = "文件类型")
    private String fileType;
	/**存储方式*/
	@Excel(name = "存储方式", width = 15)
    @ApiModelProperty(value = "存储方式")
    private String type;
	/**业务id*/
	@Excel(name = "业务id", width = 15)
    @ApiModelProperty(value = "业务id")
    private String businessId;
	/**业务表名*/
	@Excel(name = "业务表名", width = 15)
    @ApiModelProperty(value = "业务表名")
    private String businessTableName;
    /**拼接好的文件路径*/
    @Excel(name = "拼接好的文件路径", width = 15)
    @ApiModelProperty(value = "拼接好的文件路径")
    @TableField(exist = false)
    private String splicFilePath;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "更新日期")
    private Date updateTime;
	/**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private String sysOrgCode;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private String updateBy;
	/**删除标志*/
	@Excel(name = "删除标志", width = 15)
    @ApiModelProperty(value = "删除标志")
    private Integer delFlag;

    /**文件大小*/
    @TableField(exist = false)
    @ApiModelProperty(value = "文件大小")
    private Long fileSize;
}
