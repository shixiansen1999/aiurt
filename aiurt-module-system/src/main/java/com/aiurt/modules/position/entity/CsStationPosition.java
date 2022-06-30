package com.aiurt.modules.position.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.aiurt.common.aspect.annotation.Dict;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: cs_station_position
 * @Author: jeecg-boot
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@Data
@TableName("cs_station_position")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="cs_station_position对象", description="cs_station_position")
public class CsStationPosition implements Serializable {
    private static final long serialVersionUID = 1L;

	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
    private String id;
	/**所属站所*/
	@Excel(name = "所属站所", width = 15)
    @ApiModelProperty(value = "所属站所")
    private String staionCode;
	/**说明*/
	@Excel(name = "说明", width = 15)
    @ApiModelProperty(value = "说明")
    private String remark;
	/**删除标志*/
	@Excel(name = "删除标志", width = 15)
    @ApiModelProperty(value = "删除标志")
    private Integer delFlag;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
	/**更新时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
	/**所属线路*/
	@Excel(name = "所属线路", width = 15)
    @ApiModelProperty(value = "所属线路")
    private String lineCode;
    /**线路名称*/
    @ApiModelProperty(value = "线路名称")
    @TableField(exist = false)
    private String lineName;
	/**上级节点*/
	@Excel(name = "上级节点", width = 15)
    @ApiModelProperty(value = "上级节点")
    private String pid;
	/**位置名称*/
	@Excel(name = "位置名称", width = 15)
    @ApiModelProperty(value = "位置名称")
    private String positionName;
	/**位置编码*/
	@Excel(name = "位置编码", width = 15)
    @ApiModelProperty(value = "位置编码")
    private String positionCode;
	/**位置类型(9:室/10:支柱)*/
	@Excel(name = "位置类型(9:室/10:支柱)", width = 15)
    @ApiModelProperty(value = "位置类型(9:室/10:支柱)")
    @Dict(dicCode = "station_level_three")
    private Integer positionType;
    /**用于手动翻译*/
    @Dict(dicCode = "station_level")
	@TableField(exist = false)
    private String positionType_dictText;

	/**距离首站/站点位置(单位：m)*/
	@Excel(name = "距离首站/站点位置(单位：m)", width = 15)
    @ApiModelProperty(value = "距离首站/站点位置(单位：m)")
    private Double distanceStation;
	/**层级结构(/A/,/A/B/,/A/C/)*/
	@Excel(name = "层级结构(/A/,/A/B/,/A/C/)", width = 15)
    @ApiModelProperty(value = "层级结构(/A/,/A/B/,/A/C/)")
    private String positionCodeCc;
    /**排序*/
    @Excel(name = "排序", width = 15)
    @ApiModelProperty(value = "排序")
    private Integer sort;
    /**级别*/
    @ApiModelProperty(value = "级别")
    @TableField(exist = false)
    private Integer level;
    /**子节点*/
    @ApiModelProperty(value = "子节点")
    @TableField(exist = false)
    private List<CsStationPosition> children;
    /**上级位置*/
    @ApiModelProperty(value = "上级位置")
    @TableField(exist = false)
    private String pUrl;
    /**用于过滤查询*/
    @ApiModelProperty(value = "用于过滤查询")
    @TableField(exist = false)
    private String code;
}
