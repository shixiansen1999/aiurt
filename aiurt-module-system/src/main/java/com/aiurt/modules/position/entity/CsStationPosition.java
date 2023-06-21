package com.aiurt.modules.position.entity;

import com.aiurt.common.aspect.annotation.Dict;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

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
    @TableLogic
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
    /**经度*/
    @Excel(name = "经度", width = 15)
    @ApiModelProperty(value = "经度")
    private BigDecimal longitude;
    /**纬度*/
    @Excel(name = "纬度", width = 15)
    @ApiModelProperty(value = "纬度")
    private BigDecimal latitude;
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
    /**位置电话*/
    @Excel(name = "位置电话", width = 15)
    @ApiModelProperty(value = "位置电话")
    @TableField(exist = false)
    private String phoneNum;
	/**位置类型(9:室/10:支柱)*/
	@Excel(name = "位置类型(9:室/10:支柱)", width = 15)
    @ApiModelProperty(value = "位置类型(9:室/10:支柱)")
    @Dict(dicCode = "station_level_three")
    private Integer positionType;
    @Excel(name = "位置类型", width = 15)
	@TableField(exist = false)
	String positionTypeName;
    /**用于手动翻译*/
    @Dict(dicCode = "station_level")
	@TableField(exist = false)
    private String positionTypes;

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
    @Excel(name = "层级类型")
    @TableField(exist = false)
    private String levelName;
    /**子节点*/
    @ApiModelProperty(value = "子节点")
    @TableField(exist = false)
    private List<CsStationPosition> children;
    /**父级位置编码*/
    @ApiModelProperty(value = "父级位置编码")
    @TableField(exist = false)
    private String pCode;
    /**上（父）级位置*/
    @Excel(name = "上级位置名称")
    @ApiModelProperty(value = "上（父）级位置")
    @TableField(exist = false)
    private String pUrl;
    /**用于过滤查询*/
    @ApiModelProperty(value = "用于过滤查询")
    @TableField(exist = false)
    private String code;
    /**所有位置*/
    @ApiModelProperty(value = "所有位置")
    @TableField(exist = false)
    private String codeCc;
    /**公里标*/
    @ApiModelProperty(value = "公里标")
    private String length;
    @TableField(exist = false)
    String text;

    @TableField(exist = false)
    private String title;

    @TableField(exist = false)
    private String value;

    @ApiModelProperty(value = "搜索颜色")
    @TableField(exist = false)
    private String color;

    @JsonProperty("pId")
    @TableField(exist = false)
    private String fid;

    @TableField(exist = false)
    private Boolean isLeaf;
}
