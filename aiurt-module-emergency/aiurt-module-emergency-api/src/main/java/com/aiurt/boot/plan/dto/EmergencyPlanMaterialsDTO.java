package com.aiurt.boot.plan.dto;

import com.baomidou.mybatisplus.annotation.IdType;
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

/**
 * @Description: emergency_plan_materials
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Data
public class EmergencyPlanMaterialsDTO implements Serializable {
	/**主键id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private String id;
	/**应急预案id*/
    @ApiModelProperty(value = "应急预案id")
    private String emergencyPlanId;
    /**应急预案id*/
    @ApiModelProperty(value = "应急预案启动记录id")
    private String emergencyPlanRecordId;
	/**应急物资编号*/
    @ApiModelProperty(value = "应急物资编号")
    private String materialsCode;
	/**应急物资数量*/
    @ApiModelProperty(value = "应急物资数量")
    private Integer materialsNumber;

    /**应急物资名称*/
    @ApiModelProperty(value = "应急物资名称")
    private java.lang.String materialsName;

    /**物资分类编码*/
    @ApiModelProperty(value = "物资分类编码")
    private java.lang.String categoryCode;

    /**分类名称*/
    @ApiModelProperty(value = "分类名称")
    private java.lang.String categoryName;
    /**单位*/
    @ApiModelProperty(value = "单位")
    private java.lang.String unit;


}
