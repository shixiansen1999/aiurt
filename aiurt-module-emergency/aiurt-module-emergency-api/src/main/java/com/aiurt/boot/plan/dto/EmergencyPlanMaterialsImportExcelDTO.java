package com.aiurt.boot.plan.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Description: emergency_plan_materials
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Data
public class EmergencyPlanMaterialsImportExcelDTO implements Serializable {
	/**主键id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private String id;
	/**应急预案id*/
    @ApiModelProperty(value = "应急预案id")
    private String emergencyPlanId;
    /**物资分类编码*/
    @ApiModelProperty(value = "物资分类编码")
    private String categoryCode;
    /**分类名称*/
    @Excel(name = "应急物资分类", width = 15)
    @ApiModelProperty(value = "分类名称")
    private String categoryName;
    /**应急物资编号*/
    @ApiModelProperty(value = "应急物资编号")
    private String materialsCode;
    /**应急物资名称*/
    @Excel(name = "应急物资名称", width = 15)
    @ApiModelProperty(value = "应急物资名称")
    private String materialsName;
    /**应急物资数量*/
    @Excel(name = "应急物资数量", width = 15)
    @ApiModelProperty(value = "应急物资数量")
    private Integer materialsNumber;
    /**单位*/
    @Excel(name = "单位", width = 15)
    @ApiModelProperty(value = "单位")
    private String unit;


}
