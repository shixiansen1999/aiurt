package com.aiurt.common.result;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

@Data
public class SparePartResult {

    @Excel(name="序号",width = 15)
    @TableField(exist = false)
    private Integer serialNumber;

    /**线路*/
    @Excel(name = "线路", width = 15)
    @ApiModelProperty(value = "线路")
    private String lineName;

    /**站点*/
    @Excel(name = "站点", width = 15)
    @ApiModelProperty(value = "站点")
    private String stationName;

    /**被替换备件名*/
    @Excel(name = "被替换备件名", width = 15)
    @ApiModelProperty(value = "被替换备件名")
    private String oldSparePartName;

    /**被替换备件类型*/
    @ApiModelProperty(value = "被替换备件类型")
    private Integer oldSparePartType;

    /**被替换备件类型描述*/
    @Excel(name = "被替换备件类型", width = 15)
    @ApiModelProperty(value = "被替换备件类型描述")
    private String oldSparePartTypeDesc;

    /**被替换备件班组*/
    @Excel(name = "被替换备件班组", width = 15)
    @ApiModelProperty(value = "被替换备件班组")
    private String oldOrgId;

    /**被替换备件数量*/
    @Excel(name = "被替换备件数量", width = 15)
    @ApiModelProperty(value = "被替换备件数量")
    private Integer oldSparePartNum;

    /**替换备件名*/
    @Excel(name = "替换备件名", width = 15)
    @ApiModelProperty(value = "替换备件名")
    private String newSparePartName;

    /**替换备件类型*/
    @ApiModelProperty(value = "替换备件类型")
    private Integer newSparePartType;

    /**替换备件类型描述*/
    @Excel(name = "替换备件类型", width = 15)
    @ApiModelProperty(value = "替换备件类型描述")
    private String newSparePartTypeDesc;

    /**替换备件班组*/
    @Excel(name = "替换备件班组", width = 15)
    @ApiModelProperty(value = "替换备件班组")
    private String newOrgId;

    /**替换备件数量*/
    @Excel(name = "替换备件数量", width = 15)
    @ApiModelProperty(value = "替换备件数量")
    private Integer newSparePartNum;

}
