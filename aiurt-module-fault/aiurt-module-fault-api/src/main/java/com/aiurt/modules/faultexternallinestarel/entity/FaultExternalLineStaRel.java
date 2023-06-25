package com.aiurt.modules.faultexternallinestarel.entity;

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
import java.util.List;

/**
 * @Description: 生产调度-线路站点关联表
 * @Author: hlq
 * @Date:   2023-06-19
 * @Version: V1.0
 */
@Data
@TableName("fault_external_line_sta_rel")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="fault_external_line_sta_rel对象", description="生产调度-线路站点关联表")
public class FaultExternalLineStaRel implements Serializable {
    private static final long serialVersionUID = 1L;

	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
    private Integer id;
	/**云轨线路id*/
    @Excel(name = "线路", width = 15)
    @ApiModelProperty(value = "线路名称")
    @TableField(exist =false)
    private String lineName;
    @Excel(name = "站点名称", width = 15)
    @TableField(exist =false)
    @ApiModelProperty(value = "站点名称")
    private String stationName;
	@Excel(name = "调度子系统线路ID", width = 20)
    @ApiModelProperty(value = "云轨线路id")
    private Integer iline;
	/**车站/工区 id*/
	@Excel(name = "调度子系统车站/工区ID", width = 25)
    @ApiModelProperty(value = "车站/工区 id")
    private Integer ipos;
	/**线路code*/
    @ApiModelProperty(value = "线路code")
    private String lineCode;
	/**站点code*/
	@Excel(name = "站点code", width = 15)
    @ApiModelProperty(value = "站点code")
    private String stationCode;
	/**调度子系统线路/站点名称*/
	@Excel(name = "调度子系统线路/站点名称", width = 25)
    @ApiModelProperty(value = "云轨线路/站点")
    private String scc;
	/**通信线路/站点*/
    @ApiModelProperty(value = "线路/站点")
    private String correspondenceScc;
    @TableField(exist =false)
    @ApiModelProperty(value = "ids")
    private List<String> selections;
    /**创建人*/
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
    /**创建时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private java.util.Date createTime;
    /**更新人*/
    @ApiModelProperty(value = "更新人")
    private java.lang.String updateBy;
    /**更新时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
    private java.util.Date updateTime;
}
