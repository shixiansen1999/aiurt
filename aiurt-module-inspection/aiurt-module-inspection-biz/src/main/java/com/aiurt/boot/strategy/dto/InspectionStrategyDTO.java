package com.aiurt.boot.strategy.dto;

import com.aiurt.boot.manager.dto.InspectionCodeDTO;
import com.aiurt.boot.standard.entity.InspectionCode;
import com.aiurt.boot.strategy.entity.InspectionStrategy;
import com.aiurt.modules.device.entity.Device;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.util.List;

/**
 * @projectName: aiurt-platform
 * @package: com.aiurt.boot.strategy.dto
 * @className: InspectionStrategyDTO
 * @author: life-0
 * @date: 2022/7/1 10:53
 * @description: TODO
 * @version: 1.0
 */
@Data
public class InspectionStrategyDTO extends InspectionStrategy {
    @ApiModelProperty(value = "选择的设备集合")
    @TableField(exist = false)
    List<Device> devices;
    @ApiModelProperty(value = "选择的标准表codes")
    @TableField(exist = false)
    List<String> inspectionCodes;
    @ApiModelProperty(value = "选择的设备集合")
    @TableField(exist = false)
    List<InspectionCodeDTO> inspectionCodeDTOS;
    /**专业code*/
    @Excel(name = "专业code", width = 15)
    @ApiModelProperty(value = "专业code")
    private java.lang.String professionCode;
    /**适用系统code*/
    @Excel(name = "适用系统code", width = 15)
    @ApiModelProperty(value = "适用系统code")
    private java.lang.String subsystemCode;
    @Excel(name = "适用系统名称", width = 15)
    @ApiModelProperty(value = "适用系统名称")
    @TableField(exist = false)
    private java.lang.String subsystemName;
    @Excel(name = "专业名称", width = 15)
    @ApiModelProperty(value = "专业名称")
    @TableField(exist = false)
    private java.lang.String professionName;
    @Excel(name = "站点名称", width = 15)
    @ApiModelProperty(value = "站点名称")
    @TableField(exist = false)
    private java.lang.String siteName;
    @Excel(name = "组织名称", width = 15)
    @ApiModelProperty(value = "组织名称")
    @TableField(exist = false)
    private java.lang.String mechanismName;
    @ApiModelProperty(value = "站点Code")
    @TableField(exist = false)
    private String siteCode;
    @Excel(name = "组织名称", width = 15)
    @ApiModelProperty(value = "组织code")
    @TableField(exist = false)
    private String mechanismCode;
    @Excel(name = "站点名称", width = 15)
    @ApiModelProperty(value = "多选站点集合")
    @TableField(exist = false)
    List<String> siteCodes;
    @Excel(name = "组织名称", width = 15)
    @ApiModelProperty(value = "多选组织集合")
    @TableField(exist = false)
    List<String> mechanismCodes;
    @Excel(name = "标准表Ids", width = 15)
    @ApiModelProperty(value = "标准表Ids")
    @TableField(exist = false)
    private String ids;
    private String ws;
    private String ts;
}
