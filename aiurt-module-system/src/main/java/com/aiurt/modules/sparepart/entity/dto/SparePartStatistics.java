package com.aiurt.modules.sparepart.entity.dto;

import com.aiurt.modules.material.entity.MaterialBaseType;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.util.List;

/**
 * @Author zwl
 * @Date 2022/10/17
 * @Version 1.0
 */
@Data
public class SparePartStatistics {


    /**子系统编码*/
    @Excel(name = "子系统编码", width = 15)
    @ApiModelProperty(value = "子系统编码")
    @TableField(exist = false)
    private String systemCode;

    /**子系统名称*/
    @Excel(name = "子系统名称", width = 15)
    @ApiModelProperty(value = "子系统名称")
    @TableField(exist = false)
    private String systemName;

    /**子系统名称*/
    @ApiModelProperty(value = "二级库数量")
    @TableField(exist = false)
    private Long twoCount;

    /**子系统名称*/
    @ApiModelProperty(value = "三级库数量")
    @TableField(exist = false)
    private Long threeCount;

    /**子系统人员*/
    @ApiModelProperty(value = "子系统下的物资分类")
    @TableField(exist = false)
    private List<MaterialBaseType> materialBaseTypeList;

}
