package com.aiurt.boot.materials.dto;

import com.aiurt.common.system.base.entity.DynamicTableDataEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @author fgw
 */
@Data
@ApiModel(value = "")
public class PatrolRecordDetailDTO extends DynamicTableDataEntity implements Serializable {
    private static final long serialVersionUID = -3171094546027650594L;

    /**
     * 巡检日期
     */
    @ApiModelProperty(value = "巡检日期")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date patrolDate;

    /**
     * 异常情况记录
     */
    @ApiModelProperty(value = "异常情况记录")
    private String abnormalCondition;

    /**
     * 巡视班组名称
     */
    @ApiModelProperty(value = "巡视班组名称")
    private String patrolTeamName;


    /**
     * 巡视人名称
     */
    @ApiModelProperty(value = "巡视人名称")
    private java.lang.String patrolName;

    /**
     * 物资分类名称
     */
    @ApiModelProperty(value = "物资分类名称")
    private java.lang.String categoryName;

    /**
     * 物资名称
     */
    @ApiModelProperty(value = "物资名称")
    private java.lang.String materialsName;

    /**
     * 存放地点
     */
    @ApiModelProperty(value = "存放地点")
    private java.lang.String storageLocationName;

    /**
     * 规格型号
     */
    @ApiModelProperty(value = "规格型号")
    private java.lang.String specification;

    /**
     * 数量
     */
    @ApiModelProperty(value = "数量")
    private Integer number;
}
