package org.jeecg.common.system.vo;

import com.aiurt.common.aspect.annotation.SystemFilterColumn;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
public class CsSubsystemModel implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
    private String id;
    /**
     * 名称
     */
    @Excel(name = "名称", width = 15)
    @ApiModelProperty(value = "名称")
    private String systemName;
    /**
     * 名称
     */
    @Excel(name = "简称", width = 15)
    @ApiModelProperty(value = "简称")
    private String shortenedForm;
    /**
     * 编号
     */
    @Excel(name = "编号", width = 15)
    @ApiModelProperty(value = "编号")
    @SystemFilterColumn
    private String systemCode;
    /**
     * 说明
     */
    @Excel(name = "说明", width = 15)
    @ApiModelProperty(value = "说明")
    private String description;
    /**
     * 子系统概况
     */
    @Excel(name = "子系统概况", width = 15)
    @ApiModelProperty(value = "子系统概况")
    private String generalSituation;
    /**
     * 删除标志，0未删除，1已删除
     */
    @Excel(name = "删除标志，0未删除，1已删除", width = 15)
    @ApiModelProperty(value = "删除标志，0未删除，1已删除")
    private Integer delFlag;
    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
    /**
     * 所属专业-专业表
     */
    @Excel(name = "所属专业-专业表", width = 15)
    @ApiModelProperty(value = "所属专业-专业表")
    private String majorCode;
    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    private String createBy;
    /**
     * 所属部门
     */
    @ApiModelProperty(value = "所属部门")
    private String sysOrgCode;
    /**
     * 更新人
     */
    @ApiModelProperty(value = "更新人")
    private String updateBy;
}
