package com.aiurt.boot.modules.fault.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.validation.annotation.Validated;

/**
 * @Author: swsc
 * 委外人员查询参数列表
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Validated
public class OutsourcingPersonnelParam {
    /**
     * 姓名
     */
    @Excel(name = "姓名", width = 15)
    @ApiModelProperty(value = "姓名")
    private String name;

    /**
     * 所属单位
     */
    @Excel(name = "所属单位", width = 15)
    @ApiModelProperty(value = "所属单位")
    private String company;

    /**
     * 职位名称
     */
    @Excel(name = "职位名称", width = 15)
    @ApiModelProperty(value = "职位名称")
    private String position;

    /**
     * 所属专业系统编号
     */
    @Excel(name = "所属专业系统编号", width = 15)
    @ApiModelProperty(value = "所属专业系统编号")
    private String systemCode;

    /**
     * 联系方式
     */
    @Excel(name = "联系方式", width = 15)
    @ApiModelProperty(value = "联系方式")
    private String connectionWay;

}
