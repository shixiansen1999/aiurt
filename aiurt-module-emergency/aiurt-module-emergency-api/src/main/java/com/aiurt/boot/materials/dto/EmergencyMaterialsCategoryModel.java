package com.aiurt.boot.materials.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: emergency_materials_category
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Data
public class EmergencyMaterialsCategoryModel{

    /**上级节点*/
    @Excel(name = "上级节点", width = 15)
    @ApiModelProperty(value = "上级节点")
    private String fatherName;
	/**分类编号*/
	@Excel(name = "分类编号", width = 15)
    @ApiModelProperty(value = "分类编号")
    private String categoryCode;
	/**分类名称*/
	@Excel(name = "分类名称", width = 15)
    @ApiModelProperty(value = "分类名称")
    private String categoryName;
	/**错误原因*/
	@Excel(name = "错误原因", width = 15)
    @ApiModelProperty(value = "错误原因")
    private String wrongReason;
}
