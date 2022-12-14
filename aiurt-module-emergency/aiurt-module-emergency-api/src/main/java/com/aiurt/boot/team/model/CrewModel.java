package com.aiurt.boot.team.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @author lkj
 */
@Data
public class CrewModel {
    @Excel(name = "序号", width = 15)
    private String sort;

    /**所属班次*/
    @Excel(name = "所属班次", width = 15)
    @ApiModelProperty(value = "所属班次")
    private String scheduleItem;
    /**职务*/
    @Excel(name = "职务", width = 15)
    @ApiModelProperty(value = "职务")
    private String postName;
    @Excel(name = "姓名", width = 15)
    @ApiModelProperty(value = "人员姓名")
    private String realName;
    /**负责人工号*/
    @Excel(name = "负责人工号", width = 15)
    @ApiModelProperty(value = "负责人工号")
    private String managerWorkNo;
    /**联系电话*/
    @Excel(name = "联系电话", width = 15)
    @ApiModelProperty(value = "联系电话")
    private String userPhone;
    /**所属专业*/
    @Excel(name = "所属专业", width = 15)
    @ApiModelProperty(value = "所属专业")
    private String majorName;
    /**线路站点*/
    @Excel(name = "线路站点", width = 15)
    @ApiModelProperty(value = "线路站点")
    private String lineStation;
    /**备注*/
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private String remark;

    private String mistake;
}
